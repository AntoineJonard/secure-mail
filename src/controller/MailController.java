package controller;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import javax.mail.Flags;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.Part;
import javax.mail.internet.MimeBodyPart;

import application.Main;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import javafx.stage.DirectoryChooser;
import mailing.Mailsendreceivetest;

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
        File chosenDir = dirChooser.showDialog(Main.getPrimaryStage());
        for (MimeBodyPart part : attachments){
            try {
                part.saveFile(chosenDir+File.separator+part.getFileName());
            } catch (IOException | MessagingException e) {
                e.printStackTrace();
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Error");
                alert.setHeaderText("Impossible download file(s)");
                alert.setContentText("Maybe try to reconnect.");
                alert.showAndWait();
            }
        }
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Success");
        alert.setHeaderText(attachments.size()+" file(s) have been downloaded to "+chosenDir.getPath());
        alert.showAndWait();
    }
}
