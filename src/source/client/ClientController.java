package source.client;


import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

import java.io.*;
import java.net.Socket;


public class ClientController {
    @FXML
    Button buttonSendMessage;
    @FXML
    ListView clientsList;
    @FXML
    TextArea messages;
    @FXML
    TextField messageArea;

    private DataInputStream inputStream = Main.getInputStream();
    private DataOutputStream outputStream = Main.getOutputStream();
    private Socket socket = Main.getSocket();
    private ObservableList<String> nickListItems = FXCollections.observableArrayList();


    public void sendMessage(ActionEvent event) {
        try {
            if (!messageArea.getText().trim().isEmpty()) { //checking that the message contains not only spaces
                outputStream.writeUTF(messageArea.getText());
            }
        } catch (IOException e) {
            messages.appendText("Сервер: ошибка отправки сообщения, попробуйте ещё раз.");
        }
        messageArea.clear();
        messageArea.requestFocus();
    }


    @FXML
    private void initialize() {
        Image image = new Image("resources/send.png");
        ImageView imageView = new ImageView(image);
        buttonSendMessage.setGraphic(imageView);
        start();
    }

    private void start() {
        Thread thread = new Thread(() -> {
            try {
                while (socket.isConnected()) {
                    if (inputStream.available() > 0) {
                        String strFromServer = inputStream.readUTF();
                        if (strFromServer.startsWith("/clientConnected") || strFromServer.startsWith("/clientDisconnected")) {
                            updateClientsList(strFromServer);
                        } else if (strFromServer.equals("/end")) {
                            messages.appendText("Сервер отключил Вас.");
                            break;
                        } else {
                            messages.appendText(strFromServer);
                            messages.appendText("\n");
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

    private void updateClientsList(String strFromServer) {
        String[] parts = strFromServer.split("\\s");
        System.out.println(strFromServer);
        if (parts[0].equals("/clientConnected")) {
            Platform.runLater(() -> {
                for (int i = 1; i < parts.length; i++) {
                    if (!nickListItems.contains(parts[i])) {
                        nickListItems.add(parts[i]);
                    }
                }
            });
        } else if (parts[0].equals("/clientDisconnected")) {
            Platform.runLater(() -> {
                nickListItems.retainAll(parts);
                clientsList.getItems().retainAll(nickListItems);
                clientsList.refresh();
            });
        }
        clientsList.setItems(nickListItems);
    }
}

