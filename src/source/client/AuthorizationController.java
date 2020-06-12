package source.client;


import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;


public class AuthorizationController {
    @FXML
    Button buttonLogin;
    @FXML
    Button buttonCancel;
    @FXML
    Label incorrectUsernameOrPasswordLabel;
    @FXML
    PasswordField password;
    @FXML
    TextField username;

    private DataInputStream inputStream = Main.getInputStream();
    private DataOutputStream outputStream = Main.getOutputStream();
    private Socket socket = Main.getSocket();

    public void login(ActionEvent event) {
        try {
            outputStream.writeUTF("/auth " + username.getText() + " " + password.getText());
        } catch (IOException e) {
            System.out.println("Connection error");
        }
    }


    public void cancel(ActionEvent event) {
        try {
            outputStream.close();
            inputStream.close();
            socket.close();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            Stage stage = (Stage) username.getScene().getWindow();
            stage.close();
        }
    }

    private void start() {
        Thread thread = new Thread(() -> {
            try {
                while (socket.isConnected()) {
                    if (inputStream.available() > 0) {
                        String strFromServer = inputStream.readUTF();
                        if (strFromServer.equals("/false")) {
                            Platform.runLater(() -> {
                                errorText(40.0, "Incorrect username or password.");
                                password.clear();
                            });
                        } else if (strFromServer.equals("/end")) {
                            Platform.runLater(() -> {
                                errorText(80.0, "You have been disconnected for inaction. If you want to connect, open the application again.");
                                try {
                                    outputStream.close();
                                    inputStream.close();
                                    socket.close();
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            });
                            break;
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

    private void errorText(double height, String text) {
        incorrectUsernameOrPasswordLabel.setPrefHeight(height);
        incorrectUsernameOrPasswordLabel.setTextFill(Color.RED);
        incorrectUsernameOrPasswordLabel.setText(text);
        incorrectUsernameOrPasswordLabel.setVisible(true);
    }

    @FXML
    private void initialize() {
        start();
    }

}
