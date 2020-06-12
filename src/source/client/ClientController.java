package source.client;


import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.ListView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;


public class ClientController {
    @FXML
    ListView clientsList = new ListView<>();
    @FXML
    TextArea messages;
    @FXML
    TextField messageArea;

    DataInputStream inputStream = Main.inputStream;
    DataOutputStream outputStream = Main.outputStream;
    Socket socket = Main.socket;



    public void sendMessage(ActionEvent event) {
        try {
            outputStream.writeUTF(messageArea.getText());
        } catch (IOException e) {
            messages.appendText("Сервер: ошибка отправки сообщения, попробуйте ещё раз.");
        }
        messageArea.clear();
        messageArea.requestFocus();
    }


    @FXML
    private void initialize() {
        start();
//        ObservableList<String> nickListItems = FXCollections.observableArrayList(source.server.Main.server.getClientsList());
//        clientsList.setItems(nickListItems);
//        clientsList.getSelectionModel().select(0);

        messages.autosize();
        messageArea.requestFocus();
    }



    private void start() {
        Thread thread = new Thread(() -> {
            try {
                while (socket.isConnected()) {
                    if (inputStream.available() > 0) {
                        String strFromServer = inputStream.readUTF();
                        messages.appendText(strFromServer);
                        messages.appendText("\n");
                        if (strFromServer.equalsIgnoreCase("/end")) {
                            messages.appendText("Сервер отключил Вас.");
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


}
