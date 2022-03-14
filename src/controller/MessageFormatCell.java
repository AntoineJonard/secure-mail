package controller;

import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.ListCell;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.mail.Address;
import javax.mail.Flags;
import javax.mail.Message;
import javax.mail.MessagingException;

import application.Main;

public class MessageFormatCell extends ListCell<Message> {

    public MessageFormatCell() {
    }

    @Override
    protected void updateItem(Message message, boolean b) {
        super.updateItem(message, b);

        if (message == null){
            setText("");
        }else {
            String from = "unknown";
            String subject = "unknown";
            String sentDate = "unknown";
            boolean message_seen = false;
            try {
                Address[] fromAddress = message.getFrom();
                from = fromAddress[0].toString();
                subject = message.getSubject();
                sentDate = message.getSentDate().toString();

                message_seen = message.getFlags().contains(Flags.Flag.SEEN);

            } catch (MessagingException e) {
                e.printStackTrace();
            }

            setText(from +" [ "+subject+" ] \t"+sentDate);

            setOnMouseClicked(arg0 -> showMail(message));
            
            if (message_seen){
                setFont(Font.font(null, FontWeight.NORMAL,13));
            }else {
                setFont(Font.font(null, FontWeight.BOLD,13));
            }
        }

    }

	private void showMail(Message mail) {
		try {
			FXMLLoader loader = new FXMLLoader();
			loader.setLocation(Main.class.getResource("../view/Mail.fxml"));
			
			Controller controller = new MailController(mail);
			loader.setController(controller);
			
		    Scene scene = new Scene(loader.load(), 900, 600);
		    Stage stage = new Stage();
		    stage.setTitle("Mail");
		    stage.setScene(scene);
		    stage.show();
		} catch (IOException e) {
		    Logger logger = Logger.getLogger(getClass().getName());
		    logger.log(Level.SEVERE, "Failed to create new Window.", e);
		}
	}
}
