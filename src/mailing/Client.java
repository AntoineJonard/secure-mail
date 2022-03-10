/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mailing;

import IBE.PublicParameters;
import it.unisa.dia.gas.jpbc.Element;
import it.unisa.dia.gas.jpbc.Pairing;
import it.unisa.dia.gas.plaf.jpbc.pairing.PairingFactory;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * @author imino
 */
public class Client {

    public static void main(String[] args) {


        try {

            //Service pp
            URL url = new URL("http://172.20.10.5:8080/servicePp");
            // URL url = new URL("https://www.google.com");

            URLConnection urlConn = url.openConnection();
            urlConn.setDoInput(true);
            urlConn.setDoOutput(true);
            OutputStream out = urlConn.getOutputStream();
            //out.write(user_name.getBytes());
            System.out.println("salut....");
            out.write("salut...".getBytes());

            InputStream in = urlConn.getInputStream();
            byte[] b = new byte[Integer.parseInt(urlConn.getHeaderField("Content-length"))];

            PublicParameters pp = (PublicParameters) objectFromBytes(in,b);

            Pairing pairing = PairingFactory.getPairing("IBE/a.properties");    // chargement des paramètres de la courbe elliptique

            System.out.println("Parameters from server :" + pp.getP(pairing));

            in.close();
            out.close();

            // Service sk
            url = new URL("http://172.20.10.5:8080/serviceSk?email=cryptoav.tp@gmail.com");
            // URL url = new URL("https://www.google.com");

            urlConn = url.openConnection();
            urlConn.setDoInput(true);
            urlConn.setDoOutput(true);
            out = urlConn.getOutputStream();
            //out.write(user_name.getBytes());
            System.out.println("salut....");
            out.write("salut...".getBytes());

            in = urlConn.getInputStream();
            b = new byte[Integer.parseInt(urlConn.getHeaderField("Content-length"))];
            in.read(b);

            System.out.println("Sk from server :" + pairing.getG1().newElementFromBytes(b));

        } catch (IOException ex) {
            Logger.getLogger(Client.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public static Object objectFromBytes(InputStream bytesIn, byte[] bytes){
        try (ObjectInput in = new ObjectInputStream(bytesIn)) {
            return in.readObject();
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
        return null;
    }
}
