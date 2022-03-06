package controller;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;

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

        user.setText(getMain().getUser().getEmail());

        if (getMain().downloadMails()){
            newMailCpt.setText("("+getMain().getEmails().length+" new emails)");


        }else {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText("Impossible to download emails");
            alert.setContentText("Maybe check your connection.");
            alert.showAndWait();
        }
    }
}
