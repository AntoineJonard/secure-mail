
/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package server;

import IBE.IBEBasicIdent;
import IBE.KeyPair;
import IBE.SettingParameters;
import RSAFAST.AsymmetricCryptography;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;
import it.unisa.dia.gas.jpbc.Pairing;
import it.unisa.dia.gas.plaf.jpbc.pairing.PairingFactory;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import java.io.*;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.URL;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * @author imino
 */
public class HttpServeur {

    public HashMap<String,byte[]> registeredUsers;

    public HttpServeur() throws IOException, ClassNotFoundException {

        /*
         * Retrieve users informations
         */

        URL url = HttpServeur.class.getResource("registeredUsers");
        File save = new File(url.getPath());

        FileInputStream fileInputStream = new FileInputStream(save);
        ObjectInputStream objectInputStream = new ObjectInputStream(fileInputStream);

        registeredUsers = (HashMap<String, byte[]>) objectInputStream.readObject();

    }

    public void start(){

        /*
         * Start http server
         */

        try {
            System.out.println("my address:" + InetAddress.getLocalHost());
            InetSocketAddress s = new InetSocketAddress(InetAddress.getLocalHost(), 8080);

            // chargement des paramètres de la courbe elliptique
            Pairing pairing = PairingFactory.getPairing("IBE/a.properties");

            System.out.println("Setup ....");
            // génération des paramètres du système (ie: generateur, clef publique du système et clef du maitre)
            SettingParameters sp = IBEBasicIdent.setup(pairing);
            System.out.println("Paremètre du système :");
            System.out.println("generator:" + sp.getPp().getP(pairing));
            System.out.println("P_pub:" + sp.getPp().getP_pub(pairing));
            System.out.println("MSK:" + sp.getMsk());
            System.out.println("---------------------------------");

            HttpServer server = HttpServer.create(s, 1000);
            System.out.println(server.getAddress());

            // Service to get public parameters
            server.createContext("/servicePp", new HttpHandler() {
                public void handle(HttpExchange he) throws IOException {
                    byte[] bytes = getObjectBytes(sp.getPp());
                    assert bytes != null;
                    he.sendResponseHeaders(200, bytes.length);

                    // Sending plublic parameters to client
                    OutputStream os = he.getResponseBody();
                    os.write(bytes);
                    System.out.println("Public parameters sent ("+ Arrays.toString(bytes) +")");
                    os.close();
                }
            });

            // Service to get secret key generated
            server.createContext("/serviceSk", new HttpHandler() {
                public void handle(HttpExchange he) throws IOException {

                    // parameters from client
                    String parameters = he.getRequestURI().getQuery();

                    //email parameter
                    String emailId = queryToMap(parameters).get("email");

                    System.out.println("PK:" + emailId);

                    // getting hash of password + client salt
                    InputStream byteIn = he.getRequestBody();



                    // Output stream to retrieve public key
                    ObjectInputStream objectIn = new ObjectInputStream(byteIn);

                    ClientAskMessage clientAskMessage = null;

                    try {
                        clientAskMessage = (ClientAskMessage) objectIn.readObject();
                    } catch (ClassNotFoundException e) {
                        e.printStackTrace();
                    }

                    System.out.println("Public key" + clientAskMessage.getPk().toString());
                    System.out.println("Received hash :"+ Arrays.toString(clientAskMessage.getPasswordHash()));

                    // Generation of IBE secret key depending on the email id
                    System.out.println("Key generation .....");
                    KeyPair keys = null; // genération d'une paire de clefs correspondante à id
                    try {
                        keys = IBEBasicIdent.keygen(pairing, sp.getMsk(), emailId);
                    } catch (NoSuchAlgorithmException e) {
                        e.printStackTrace();
                    }

                    System.out.println("SK:" + keys.getSk());

                    byte[] bytesToSend = new byte[0];

                    if (registeredUsers.containsKey(emailId) && clientAskMessage.getPasswordHash() != registeredUsers.get(emailId)){
                        bytesToSend = new byte[0];
                        System.out.println("Error while authenticating");
                    }else {
                        if (!registeredUsers.containsKey(emailId)){
                            /*
                             * Register new client
                             */
                            System.out.println("Registering new client");

                            registeredUsers.put(emailId,clientAskMessage.getPasswordHash());

                            URL url = HttpServeur.class.getResource("registeredUsers");
                            File save = new File(url.getPath());
                            FileOutputStream fileOutputStream = new FileOutputStream(save,false);
                            ObjectOutputStream objectOutputStream = new ObjectOutputStream(fileOutputStream);
                            objectOutputStream.writeObject(registeredUsers);
                        }

                        System.out.println("Encryption of client secret key");
                        // Encryption of secret key with public key of the client before sending to client
                        AsymmetricCryptography rsa = null;
                        try {
                            rsa = new AsymmetricCryptography();
                        } catch (NoSuchAlgorithmException | NoSuchPaddingException e) {
                            e.printStackTrace();
                        }

                        try {
                            assert rsa != null;
                            bytesToSend = rsa.encryptBytes(keys.getSk().toBytes(),clientAskMessage.getPk());
                        } catch (IllegalBlockSizeException | BadPaddingException | InvalidKeyException e) {
                            e.printStackTrace();
                        }
                    }

                    // Sending encrypted public key to client
                    he.sendResponseHeaders(200, bytesToSend.length);
                    OutputStream os = he.getResponseBody();
                    os.write(bytesToSend);
                    System.out.println("Secret key sent");
                    os.close();
                }
            });

            server.start();
        } catch (IOException ex) {
            Logger.getLogger(HttpServeur.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

    public static void main(String[] args) throws IOException, ClassNotFoundException {

        HttpServeur httpServeur = new HttpServeur();
        httpServeur.start();
    }

    public static Map<String, String> queryToMap(String query) {
        if(query == null) {
            return null;
        }
        Map<String, String> result = new HashMap<>();
        for (String param : query.split("&")) {
            String[] entry = param.split("=");
            if (entry.length > 1) {
                result.put(entry[0], entry[1]);
            }else{
                result.put(entry[0], "");
            }
        }
        return result;
    }

    public static byte[] getObjectBytes(Object obj){
        try (ByteArrayOutputStream bos = new ByteArrayOutputStream()) {
            ObjectOutputStream out = new ObjectOutputStream(bos);
            out.writeObject(obj);
            out.flush();

            return bos.toByteArray();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
}
