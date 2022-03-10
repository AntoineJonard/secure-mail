package IBE;

import it.unisa.dia.gas.jpbc.Pairing;
import it.unisa.dia.gas.plaf.jpbc.pairing.PairingFactory;
import mailing.Mailsendreceivetest;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author imino / Clément Decroix
 */
public class TestIBEAES {

    /**
     * @param args the command line arguments
     */
   
    public static void IBEalltypeoffilesEncryptionDecryption(Pairing pairing, SettingParameters sp, KeyPair keys, String filepath) {

        try {
            FileInputStream in = new FileInputStream(filepath); // ouverture d'un stream de lecture sur le fichier

            byte[] filebytes = new byte[in.available()]; // réservation d'un tableau de byte en fontion du nombre de bytes contenus dans  le fichier

            System.out.println("taille de fichier en byte:" + filebytes.length);

            in.read(filebytes); // lecture du fichier
            
            System.out.println("---------------------");
            
            System.out.println("Encryption ....");
   
            IBEcipher ibecipher = IBEBasicIdent.IBEencryption(pairing, sp.getPp(), filebytes, keys.getPk()); // chiffrement BasicID-IBE/AES
            
            File f1 = new File("MyFiles/encryptionresult" + filepath.substring(filepath.lastIndexOf("."))); // création d'un fichier pour l'enregistrement du résultat du chiffrement
            f1.createNewFile();
            FileOutputStream fout1 = new FileOutputStream(f1);
            fout1.write(ibecipher.getAescipher()); // ecriture du résultat du chiffrement dans le fichier 
            System.out.println("to access the resulting encryption file check the following path: " + f1.getAbsolutePath());
            fout1.close();
            
            System.out.println("---------------------");
   
            System.out.println("Decryption ....");
   
            byte[] resulting_bytes = IBEBasicIdent.IBEdecryption(pairing, keys.getSk(), ibecipher); //déchiffrement Basic-ID IBE/AES

            File f2 = new File("MyFiles/decryptionresult" + filepath.substring(filepath.lastIndexOf("."))); // création d'un fichier pour l'enregistrement du résultat du déchiffrement
            f2.createNewFile();
            FileOutputStream fout2 = new FileOutputStream(f2);
            fout2.write(resulting_bytes); // ecriture du résultat de déchiffrement dans le fichier 
            System.out.println("to access the resulting decryption file check the following path: " + f2.getAbsolutePath());
            fout2.close();
            
        } catch (NoSuchAlgorithmException ex) {
            Logger.getLogger(TestIBEAES.class.getName()).log(Level.SEVERE, null, ex);
        } catch (NoSuchPaddingException ex) {
            Logger.getLogger(TestIBEAES.class.getName()).log(Level.SEVERE, null, ex);
        } catch (InvalidKeyException ex) {
            Logger.getLogger(TestIBEAES.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IllegalBlockSizeException ex) {
            Logger.getLogger(TestIBEAES.class.getName()).log(Level.SEVERE, null, ex);
        } catch (BadPaddingException ex) {
            Logger.getLogger(TestIBEAES.class.getName()).log(Level.SEVERE, null, ex);
        } catch (UnsupportedEncodingException ex) {
            Logger.getLogger(TestIBEAES.class.getName()).log(Level.SEVERE, null, ex);
        } catch (FileNotFoundException ex) {
            Logger.getLogger(TestIBEAES.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(TestIBEAES.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

    public static void main(String[] args) {
        // TODO code application logic here

        Pairing pairing = PairingFactory.getPairing("IBE/a.properties"); // chargement des paramètres de la courbe elliptique 
                                                                            // la configuration A offre un pairing symmetrique 
        																	// ce qui correspond à l'implementation du schema basicID
                                                                            // qui est basé sur l'utilisation du pairing symmetrique
        System.out.println("Setup ....");
     
        SettingParameters sp = IBEBasicIdent.setup(pairing); // génération des paramètres du système (ie: generateur, clef publique du système et clef du maitre)

        System.out.println("Paremètre du système :");
        
        System.out.println("generator:" + sp.getPp().getP());

        System.out.println("P_pub:" + sp.getPp().getP_pub());

        System.out.println("MSK:" + sp.getMsk());

        String id = "cryptoav.tp@gmail.com"; // id de test 

        System.out.println("-----------------------------");
        
        try {
            System.out.println("Key generation .....");
     
            KeyPair keys = IBEBasicIdent.keygen(pairing, sp.getMsk(), id); // genération d'une paire de clefs correspondante à id

            System.out.println("PK:" + keys.getPk());

            System.out.println("SK:" + keys.getSk());
        
            System.out.println("-----------------------------");
            
            URL url = TestIBEAES.class.getResource("files/SecretFile.txt");
            System.out.println(url.toString());
            assert url != null;
            String path = url.getPath();
            
            IBEalltypeoffilesEncryptionDecryption(pairing, sp, keys, path); // chiffrement/déchiffrement
        
            System.out.println("Fin ....");
   
        } catch (NoSuchAlgorithmException ex) {
            Logger.getLogger(TestIBEAES.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

}
