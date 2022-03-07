package controller;

import application.User;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
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

        if (getMain().connectAs(user)){
            getMain().showMailboxView();
        }else {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText("Impossible to connect to server");
            alert.setContentText("Maybe your logins are bad.");
            alert.showAndWait();
        }
    }
}
