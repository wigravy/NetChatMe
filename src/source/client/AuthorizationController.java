package source.client;


import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;


public class AuthorizationController {
    @FXML
    Label incorrectUsernameOrPasswordLabel;
    @FXML
    PasswordField password;

    @FXML
    TextField username;

    DataInputStream inputStream = Main.inputStream;
    DataOutputStream outputStream = Main.outputStream;
    Socket socket = Main.socket;

    public void login(ActionEvent event) {
        try {
            outputStream.writeUTF("/auth " + username.getText() + " " + password.getText());
        } catch (IOException e) {
            incorrectUsernameOrPasswordLabel.setVisible(true);
        }
    }

    public void cancel(ActionEvent event) {
        try {
            outputStream.close();
            inputStream.close();
            socket.close();
            Stage stage = (Stage) username.getScene().getWindow();
            stage.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void start() {
        Thread thread = new Thread(() -> {
            try {
                while (socket.isConnected()) {
                    if (inputStream.available() > 0) {
                        String strFromServer = inputStream.readUTF();
                        if (strFromServer.equals("false")) {
                            incorrectUsernameOrPasswordLabel.setVisible(true);
                            password.clear();
                        } else {
                            Main.setRoot("Client");
                            break;
                        }
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
        thread.setDaemon(true);
        thread.start();
    }




    @FXML
    public void initialize() {
        start();
    }
}
