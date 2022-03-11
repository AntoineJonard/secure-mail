package controller;

import IBE.IBEBasicIdent;
import IBE.IBEcipher;
import IBE.PublicParameters;
import application.Main;
import it.unisa.dia.gas.jpbc.Pairing;
import it.unisa.dia.gas.plaf.jpbc.pairing.PairingFactory;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import java.io.*;
import java.net.URL;
import java.net.URLConnection;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;

public class SendMailController extends Controller {

	@FXML
	private TextField recipient;
	@FXML
	private TextField subject;
	@FXML 
	private TextField message;
	@FXML
	private HBox attachments;

	private MimeMessage mail;

	private List<String> attachmentsPaths;

	public SendMailController() {
		super();
		attachmentsPaths = new ArrayList<>();
	}

	@Override
	public void setMain(Main main) {
		super.setMain(main);

		Session session = Session.getInstance(getMain().getSendProperties(), new javax.mail.Authenticator() {
			protected PasswordAuthentication getPasswordAuthentication() {
				return new PasswordAuthentication(getMain().getUser().getEmail(), getMain().getUser().getPassword());
			}
		});

		mail = new MimeMessage(session);
	}

	@FXML
	private void send(MouseEvent event) {

		if (
				subject.getText().isEmpty()
				|| recipient.getText().isEmpty()
				|| message.getText().isEmpty()
		) {
			Alert alert = new Alert(Alert.AlertType.ERROR);
			alert.setTitle("Field are not valids");
			alert.setHeaderText("Impossible to send mail with empty fields");
			alert.setContentText("Please enter a recipient, a subject, and a message.");
			alert.showAndWait();
		}else {
			try {
				mail.setFrom(getMain().getUser().getEmail());
				mail.addRecipient(Message.RecipientType.TO, new InternetAddress(recipient.getText()));
				mail.setSubject(subject.getText());

				Multipart myemailcontent = new MimeMultipart();
				MimeBodyPart bodypart = new MimeBodyPart();
				bodypart.setText(message.getText());

				if (!attachmentsPaths.isEmpty()){
					MimeBodyPart attachementfile = new MimeBodyPart();
					for (String attachement_path : attachmentsPaths){

						File file = new File(attachement_path);

						FileInputStream in = new FileInputStream(file);

						byte[] filebytes = new byte[in.available()];

						in.read(filebytes);

						URL url = new URL("http://"+Main.getServerConfig().getAdress()+":"+Main.getServerConfig().getPort()+"/servicePp");

						URLConnection urlConn = url.openConnection();
						urlConn.setDoInput(true);
						urlConn.setDoOutput(false);

						InputStream urlConnInputStream = urlConn.getInputStream();
						ObjectInputStream objectInputStream = new ObjectInputStream(urlConnInputStream);

						PublicParameters pp = (PublicParameters) objectInputStream.readObject();

						System.out.println("Parameters from server :" + pp.getP(Main.getPairing()));

						urlConnInputStream.close();

						IBEcipher ibecipher = IBEBasicIdent.IBEencryption(Main.getPairing(), pp, filebytes, recipient.getText());

						File f1 = new File("MyFiles/encryptionSender");
						if (f1.createNewFile()){
							FileOutputStream fout1 = new FileOutputStream(f1);
							ObjectOutputStream objectOutputStream = new ObjectOutputStream(fout1);
							objectOutputStream.writeObject(ibecipher); // ecriture du résultat du chiffrement dans le fichier
							System.out.println("to access the resulting encryption file check the following path: " + f1.getAbsolutePath());
							fout1.close();
							attachementfile.attachFile(f1);
						}else {
							System.out.println("Error while encryting file, sending uncrypted file");
							attachementfile.attachFile(attachement_path);
						}
					}
					myemailcontent.addBodyPart(bodypart);
					myemailcontent.addBodyPart(attachementfile);
				}

				mail.setContent(myemailcontent);
				Transport.send(mail);

				Alert alert = new Alert(Alert.AlertType.INFORMATION);
				alert.setTitle("Email sent");
				alert.setHeaderText("Your email successfully reached its destination");
				alert.showAndWait();

			} catch (MessagingException | IOException e) {
				e.printStackTrace();

				Alert alert = new Alert(Alert.AlertType.ERROR);
				alert.setTitle("Error");
				alert.setHeaderText("Impossible to send mail");
				alert.setContentText("Maybe check that your attachments exists on your system and that all fields have correct value.");
				alert.showAndWait();
			} catch (NoSuchPaddingException | IllegalBlockSizeException | NoSuchAlgorithmException | BadPaddingException | InvalidKeyException | ClassNotFoundException e) {
				e.printStackTrace();
			}
		}
	}
	
	@FXML
	private void attach(MouseEvent event) {
		FileChooser fileChooser = new FileChooser();
		fileChooser.setTitle("Select attachment");
		fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("All Files", "*.*"));
		File selectedFile = fileChooser.showOpenDialog(Main.getPrimaryStage());

		attachmentsPaths.add(selectedFile.getPath());

		Label label = new Label();

		label.setText(selectedFile.getName());
		label.setTextFill(Color.WHITE);
		label.setStyle("-fx-background-color: #a0a0a0;"
				+ "-fx-padding: 5px;"
				+ "-fx-background-radius: 15 15 15 15;");
		label.setFont(new Font(null, 20));

		attachments.getChildren().add(label);
	}
}
