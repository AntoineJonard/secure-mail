package controller;

import java.io.*;
import java.net.URL;
import java.net.URLConnection;
import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.Part;
import javax.mail.internet.MimeBodyPart;

import IBE.IBEBasicIdent;
import IBE.IBEcipher;
import RSAFAST.AsymmetricCryptography;
import RSAFAST.GenerateKeys;
import application.Main;
import it.unisa.dia.gas.jpbc.Element;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import javafx.stage.DirectoryChooser;
import server.ClientAskMessage;

public class MailController extends Controller{
	
	@FXML
	private Label title;
	@FXML
	private Label date;
	@FXML
	private Label from;
	@FXML
	private Label content;
	@FXML
	private Button dlButton;
	@FXML
	private Label nbAttachments;
	
	private final Message mail;
    private final List<MimeBodyPart> attachments;

    public MailController(Message mail) {
		super();
		this.mail = mail;
        attachments = new ArrayList<>();
	}

	@FXML
    private void initialize() {
    	try {
			title.setText(mail.getSubject().toString());
			date.setText(mail.getSentDate().toString());
			from.setText(mail.getFrom()[0].toString());
			
			String contentType = mail.getContentType();
            String messageContent = "";
            StringBuilder attachmentsNames = new StringBuilder();

            int cptAttachFiles = 0;

            if (contentType.contains("multipart")) {
                // content may contain attachments
                Multipart multiPart = (Multipart) mail.getContent();
                int numberOfParts = multiPart.getCount();
                for (int partCount = 0; partCount < numberOfParts; partCount++) {
                    MimeBodyPart part = (MimeBodyPart) multiPart.getBodyPart(partCount);
                    if (Part.ATTACHMENT.equalsIgnoreCase(part.getDisposition())) {
                        attachments.add(part);
                        attachmentsNames.append(part.getFileName()).append("; ");
                        cptAttachFiles++;

                    } else {
                        // this part may be the message content
                        messageContent = part.getContent().toString();
                    }
                }
            } else if (contentType.contains("text/plain")
                    || contentType.contains("text/html")) {
                Object content = mail.getContent();
                if (content != null) {
                    messageContent = content.toString();
                }
            }
            
            content.setText(messageContent);
			
            nbAttachments.setText(cptAttachFiles+ " attachments : "+ attachmentsNames);

            if (cptAttachFiles <= 0){
                dlButton.setDisable(true);
            }
            
		} catch (MessagingException | IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    }
	
    @FXML
    private void download(MouseEvent event)  {
        DirectoryChooser dirChooser = new DirectoryChooser();
        dirChooser.setTitle("Select download directory");
        File chosenDir = dirChooser.showDialog(Main.getInstance().getPrimaryStage());
        for (MimeBodyPart part : attachments){
            try {
                File encryptedFile = new File("MyFiles/rEncrypted"+part.getFileName()+(new Random().nextInt(100)));
                part.saveFile(encryptedFile);
                FileInputStream fileInputStream = new FileInputStream(encryptedFile);
                ObjectInputStream objectInputStream = new ObjectInputStream(fileInputStream);
                IBEcipher ibEcipher = (IBEcipher) objectInputStream.readObject();

                // Getting user encrypted secret key from server
                GenerateKeys gk = null;
                try {
                    gk = new GenerateKeys(2048);
                    gk.createKeys();
                } catch (NoSuchAlgorithmException | NoSuchProviderException e) {
                    System.err.println(e.getMessage());
                }

                URL url = new URL("http://"+Main.getInstance().getServerConfig().getAdress()+":"+Main.getInstance().getServerConfig().getPort()+"/serviceSk?email=cryptoav.tp@gmail.com");

                URLConnection urlConn = url.openConnection();
                urlConn.setDoInput(true);
                urlConn.setDoOutput(true);
                OutputStream out = urlConn.getOutputStream();

                MessageDigest md = MessageDigest.getInstance("SHA-1");
                md.update(Main.getInstance().getUser().getSalt());
                md.update(Main.getInstance().getUser().getPassword().getBytes(StandardCharsets.UTF_8));
                byte[] hash = md.digest();
                System.out.println("Sending password salted hash "+ Arrays.toString(hash));
                System.out.println("sending public rsa key :"+gk.getPublicKey().toString());

                ObjectOutputStream objectOut = new ObjectOutputStream(urlConn.getOutputStream());
                objectOut.writeObject(new ClientAskMessage(gk.getPublicKey(), hash));

                InputStream in = urlConn.getInputStream();
                byte[] secretKeyBytes = new byte[Integer.parseInt(urlConn.getHeaderField("Content-length"))];
                in.read(secretKeyBytes);

                AsymmetricCryptography rsa = new AsymmetricCryptography();

                Element sk = Main.getInstance().getPairing().getG1().newElementFromBytes(rsa.decryptBytes(secretKeyBytes,gk.getPrivateKey()));

                System.out.println("Sk from server :" + sk );

                in.close();
                out.close();

                byte[] resulting_bytes = IBEBasicIdent.IBEdecryption(Main.getInstance().getPairing(), sk, ibEcipher); //déchiffrement Basic-ID IBE/AES

                File decryptedfile = new File(chosenDir+File.separator+part.getFileName()); // création d'un fichier pour l'enregistrement du résultat du déchiffrement
                decryptedfile.createNewFile();
                FileOutputStream fileOutputStream = new FileOutputStream(decryptedfile);
                fileOutputStream.write(resulting_bytes);
                fileOutputStream.close();

            } catch (IOException | NumberFormatException | ClassNotFoundException | MessagingException e) {
                e.printStackTrace();
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Error");
                alert.setHeaderText("Impossible download file(s)");
                alert.setContentText("Maybe try to reconnect.");
                alert.showAndWait();
            } catch (NoSuchPaddingException | IllegalBlockSizeException | BadPaddingException | NoSuchAlgorithmException | InvalidKeyException e) {
                e.printStackTrace();
            }
        }
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Success");
        alert.setHeaderText(attachments.size()+" file(s) have been downloaded to "+chosenDir.getPath());
        alert.showAndWait();
    }
}
