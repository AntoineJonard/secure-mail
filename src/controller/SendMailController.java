package controller;

import IBE.IBEBasicIdent;
import IBE.IBEcipher;
import IBE.PublicParameters;
import application.Main;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
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
import java.util.Random;

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

						File originalFile = new File(attachement_path);

						FileInputStream in = new FileInputStream(originalFile);

						byte[] filebytes = new byte[in.available()];

						in.read(filebytes);

						URL url = new URL("https://"+Main.getInstance().getServerConfig().getAdress()+":"+Main.getInstance().getServerConfig().getPort()+"/servicePp");

						URLConnection urlConn = url.openConnection();
						urlConn.setDoInput(true);
						urlConn.setDoOutput(false);

						InputStream urlConnInputStream = urlConn.getInputStream();
						ObjectInputStream objectInputStream = new ObjectInputStream(urlConnInputStream);

						PublicParameters pp = (PublicParameters) objectInputStream.readObject();

						System.out.println("Parameters from server :" + pp.getP(Main.getInstance().getPairing()));

						urlConnInputStream.close();

						IBEcipher ibecipher = IBEBasicIdent.IBEencryption(Main.getInstance().getPairing(), pp, filebytes, recipient.getText());

						File encryptedFile = new File("MyFiles/sEncrypted"+originalFile.getName()+(new Random().nextInt(100)));
						if (encryptedFile.createNewFile()){
							FileOutputStream fileOutputStream = new FileOutputStream(encryptedFile);
							ObjectOutputStream objectOutputStream = new ObjectOutputStream(fileOutputStream);
							// Writing of ibe cipher and aes key
							objectOutputStream.writeObject(ibecipher);
							fileOutputStream.close();
							attachementfile.attachFile(encryptedFile);
							attachementfile.setFileName(originalFile.getName());
						}else {
							System.out.println("Error while encryting originalFile, sending uncrypted originalFile");
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
		File selectedFile = fileChooser.showOpenDialog(Main.getInstance().getPrimaryStage());

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
