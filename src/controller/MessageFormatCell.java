package controller;

import javafx.scene.control.ListCell;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

import javax.mail.Address;
import javax.mail.Flags;
import javax.mail.Message;
import javax.mail.MessagingException;

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

            if (message_seen){
                setFont(Font.font(null, FontWeight.NORMAL,13));
            }else {
                setFont(Font.font(null, FontWeight.BOLD,13));
            }
        }

    }
}
