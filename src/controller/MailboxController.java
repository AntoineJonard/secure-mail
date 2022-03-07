package controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.util.Callback;

import java.util.Collections;

import javax.mail.Message;

public class MailboxController extends Controller{

    @FXML
    private Label user;
    @FXML
    private Label newMailCpt;
    @FXML
    private ListView<Message> emails;

    @FXML
    private void initialize() {
        getMails();
    }

    @FXML
    private void refresh() {
        getMails();
    }

    private void getMails() {
        user.setText(getMain().getUser().getEmail());

        if (getMain().downloadMails()){
            newMailCpt.setText("("+getMain().getEmails().length+" emails)");

            ObservableList<Message> messages = FXCollections.observableArrayList(getMain().getEmails());
            Collections.reverse(messages);
            emails.setItems(messages);
            emails.setCellFactory(messageListView -> new MessageFormatCell());

        }else {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText("Impossible to download emails");
            alert.setContentText("Maybe check your connection.");
            alert.showAndWait();
        }
    }
    
    @FXML
    private void new_mail() {
    	
    }
}
