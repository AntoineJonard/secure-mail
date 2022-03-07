package controller;

import java.io.File;
import java.io.IOException;
import java.net.URL;

import javax.mail.Flags;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.Part;
import javax.mail.internet.MimeBodyPart;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
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
	private Button download;
	@FXML
	private Label nbAttachments;
	
	private Message mail;

    public MailController(Message mail) {
		super();
		this.mail = mail;
	}

	@FXML
    private void initialize() {
    	try {
			title.setText(mail.getSubject().toString());
			date.setText(mail.getSentDate().toString());
			from.setText(mail.getFrom()[0].toString());
			
			String contentType = mail.getContentType();
            String messageContent = "";
            boolean message_seen = mail.getFlags().contains(Flags.Flag.SEEN);
            // store attachment file name, separated by comma
            String attachFiles = "";
            int cptAttachFiles = 0;

            if (contentType.contains("multipart")) {
                // content may contain attachments
                Multipart multiPart = (Multipart) mail.getContent();
                int numberOfParts = multiPart.getCount();
                for (int partCount = 0; partCount < numberOfParts; partCount++) {
                    MimeBodyPart part = (MimeBodyPart) multiPart.getBodyPart(partCount);
                    if (Part.ATTACHMENT.equalsIgnoreCase(part.getDisposition())) {
                        // this part is attachment
                        String fileName = part.getFileName();
                        attachFiles += fileName + ", ";
                        
                        cptAttachFiles++;

                    } else {
                        // this part may be the message content
                        messageContent = part.getContent().toString();
                    }
                }

                if (attachFiles.length() > 1) {
                    attachFiles = attachFiles.substring(0, attachFiles.length() - 2);
                }
            } else if (contentType.contains("text/plain")
                    || contentType.contains("text/html")) {
                Object content = mail.getContent();
                if (content != null) {
                    messageContent = content.toString();
                }
            }
            
            content.setText(messageContent);
			
            nbAttachments.setText(cptAttachFiles+ " attachments");
            
		} catch (MessagingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    }
	
    @FXML
    private void download(MouseEvent event) {
     
    }

}
