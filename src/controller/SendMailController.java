package controller;

import application.Main;
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

import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import java.io.File;
import java.io.IOException;
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
		try {
			mail.setFrom(getMain().getUser().getEmail());
			mail.addRecipient(Message.RecipientType.TO, new InternetAddress(recipient.getText()));
			mail.setSubject(subject.getText());

			Multipart myemailcontent = new MimeMultipart();
			MimeBodyPart bodypart = new MimeBodyPart();
			bodypart.setText(message.getText());


			MimeBodyPart attachementfile = new MimeBodyPart();
			for (String attachement_path : attachmentsPaths)
				attachementfile.attachFile(attachement_path);
			myemailcontent.addBodyPart(bodypart);
			myemailcontent.addBodyPart(attachementfile);
			mail.setContent(myemailcontent);
			Transport.send(mail);


		} catch (MessagingException | IOException e) {
			e.printStackTrace();

			Alert alert = new Alert(Alert.AlertType.ERROR);
			alert.setTitle("Error");
			alert.setHeaderText("Impossible to send mail");
			alert.setContentText("Maybe check that your attachments exists on your system and that all fields have correct value.");
			alert.showAndWait();
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
