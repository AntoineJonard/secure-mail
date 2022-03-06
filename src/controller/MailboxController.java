package controller;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;

import javax.mail.Message;

public class MailboxController extends Controller{

    @FXML
    private Label user;
    @FXML
    private Label newMailcpt;
    @FXML
    private ListView<Message> emails;

    @FXML
    private void initialize() {

        user.setText(getMain().getUser().getEmail());

    }
}
