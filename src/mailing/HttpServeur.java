
/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mailing;

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


    public static void main(String[] args) {

        try {
            // InetSocketAddress s = new InetSocketAddress("localhost", 8080);
            System.out.println("my address:" + InetAddress.getLocalHost());
            InetSocketAddress s = new InetSocketAddress(InetAddress.getLocalHost(), 8080);
            //  InetSocketAddress s = new InetSocketAddress("localhost", 8080);

            Pairing pairing = PairingFactory.getPairing("IBE/a.properties");    // chargement des paramètres de la courbe elliptique
            // la configuration A offre un pairing symmetrique
            // ce qui correspond à l'implementation du schema basicID
            // qui est basé sur l'utilisation du pairing symmetrique
            System.out.println("Setup ....");
            SettingParameters sp = IBEBasicIdent.setup(pairing); // génération des paramètres du système (ie: generateur, clef publique du système et clef du maitre)
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

                    // Output stream to retrieve public key
                    ObjectInputStream objectIn = new ObjectInputStream(he.getRequestBody());

                    PublicKey pk = null;

                    try {
                        pk = (PublicKey) objectIn.readObject();
                    } catch (ClassNotFoundException e) {
                        e.printStackTrace();
                    }

                    System.out.println("Public key" + pk.toString());

                    // Generation of IBE secret key depending on the email id
                    System.out.println("Key generation .....");
                    KeyPair keys = null; // genération d'une paire de clefs correspondante à id
                    try {
                        keys = IBEBasicIdent.keygen(pairing, sp.getMsk(), emailId);
                    } catch (NoSuchAlgorithmException e) {
                        e.printStackTrace();
                    }

                    System.out.println("SK:" + keys.getSk());

                    // Encryption of secret key with public key of the client before sending to client
                    AsymmetricCryptography rsa = null;
                    try {
                        rsa = new AsymmetricCryptography();
                    } catch (NoSuchAlgorithmException | NoSuchPaddingException e) {
                        e.printStackTrace();
                    }

                    byte[] skBytes = null;
                    try {
                        skBytes = rsa.encryptBytes(keys.getSk().toBytes(),pk);
                    } catch (IllegalBlockSizeException | BadPaddingException | InvalidKeyException e) {
                        e.printStackTrace();
                    }

                    // Sending encrypted public key to client
                    he.sendResponseHeaders(200, skBytes.length);
                    OutputStream os = he.getResponseBody();
                    os.write(skBytes);
                    System.out.println("Secret key sent");
                    os.close();
                }
            });

            server.start();
        } catch (IOException ex) {
            Logger.getLogger(HttpServeur.class.getName()).log(Level.SEVERE, null, ex);
        }
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
