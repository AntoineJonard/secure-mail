
/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mailing;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;

import IBE.IBEBasicIdent;
import IBE.KeyPair;
import IBE.SettingParameters;
import it.unisa.dia.gas.jpbc.Pairing;
import it.unisa.dia.gas.plaf.jpbc.pairing.PairingFactory;

import java.io.IOException;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Executors;
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
            
            Pairing pairing = PairingFactory.getPairing("IBE/a.properties"); 	// chargement des paramètres de la courbe elliptique 
																	            // la configuration A offre un pairing symmetrique 
																				// ce qui correspond à l'implementation du schema basicID
																	            // qui est basé sur l'utilisation du pairing symmetrique
			System.out.println("Setup ....");
			SettingParameters sp = IBEBasicIdent.setup(pairing); // génération des paramètres du système (ie: generateur, clef publique du système et clef du maitre)
			System.out.println("Paremètre du système :");
			System.out.println("generator:" + sp.getPp().getP());
			System.out.println("P_pub:" + sp.getPp().getP_pub());
			System.out.println("MSK:" + sp.getMsk());
			System.out.println("---------------------------------");
			
            
            HttpServer server = HttpServer.create(s, 1000);
            System.out.println(server.getAddress());
            server.createContext("/servicePp", new HttpHandler()
              {
                public void handle(HttpExchange he) throws IOException {
                    byte[] bytes1 = new byte[Integer.parseInt(he.getRequestHeaders().getFirst("Content-length"))];
                    he.getRequestBody().read(bytes1);
                    String msg = new String(bytes1);
                    System.out.println("message reçu " + msg);
                    byte[] bytes = "bonjour client ..".getBytes();
                    he.sendResponseHeaders(200, bytes.length);
                    OutputStream os = he.getResponseBody();
                    os.write(bytes);
                    System.out.println("sending response done....");
                    os.close();
                }
            });
            
            
            server.createContext("/serviceSk", new HttpHandler()
            {
              public void handle(HttpExchange he) throws IOException {
                  byte[] bytes1 = new byte[Integer.parseInt(he.getRequestHeaders().getFirst("Content-length"))];
                  he.getRequestBody().read(bytes1);
                  String id = new String(bytes1);
                  System.out.println("message reçu : " + id);
                  
                  
                  System.out.println("Key generation .....");
                  //KeyPair keys = IBEBasicIdent.keygen(pairing, sp.getMsk(), id); // genération d'une paire de clefs correspondante à id
                  //System.out.println("PK:" + keys.getPk());
                  //System.out.println("SK:" + keys.getSk());
                  
                  byte[] bytes = "bonjour client ..".getBytes();
                  he.sendResponseHeaders(200, bytes.length);
                  OutputStream os = he.getResponseBody();
                  os.write(bytes);
                  System.out.println("sending response done....");
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

}
