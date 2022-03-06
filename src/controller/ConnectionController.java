package controller;

import application.User;
import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;

public class ConnectionController extends Controller {

    @FXML
    private TextField email;
    @FXML
    private TextField password;

    @FXML
    private void connect(MouseEvent event) {
        User user = new User(email.getText(),password.getText());

        getMain().setUser(user);

        getMain().showMailboxView();
    }

}
