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
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;


public class ClientController {
    @FXML
    Button buttonSendMessage;
    @FXML
    ListView<String> clientsList;
    @FXML
    TextArea messages;
    @FXML
    TextField messageArea;

    private File history = new File("src/resources/history.txt");
    private final int HISTORY_CAPACITY = 100; //This means 100 lines of text.

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
            messages.appendText("Server: error sending message, try again");
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
                loadChatHistory();
                while (socket.isConnected()) {
                    if (inputStream.available() > 0) {
                        String strFromServer = inputStream.readUTF();
                        if (strFromServer.startsWith("/clientConnected") || strFromServer.startsWith("/clientDisconnected")) {
                            updateClientsList(strFromServer);
//                        } else if (strFromServer.startsWith("/update")) {
//
                        } else if (strFromServer.equals("/end")) {
                            messages.appendText("Сервер отключил Вас.");
                            break;
                        } else {
                            messages.appendText(strFromServer);
                            saveChatHistory(strFromServer);
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

    private void saveChatHistory(String message) {
        message += "\n";
        try {
            FileWriter fileWriter = new FileWriter(history, true);
            fileWriter.write(message);
            fileWriter.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void loadChatHistory() {
        try {
            if (!history.isFile()) {
                history.createNewFile();
            }

            FileReader fileReader = new FileReader(history);
            BufferedReader bufferedReader = new BufferedReader(fileReader);
            String line = bufferedReader.readLine();
            Path path = Paths.get(String.valueOf(history));
            long skipLines = Files.lines(path).count();
            if (skipLines > HISTORY_CAPACITY) {
                skipLines -= HISTORY_CAPACITY;
            }
            while (line != null) {
                if (skipLines == 0) {
                    messages.appendText(line);
                    messages.appendText("\n");
                    line = bufferedReader.readLine();
                } else {
                    line = bufferedReader.readLine();
                    skipLines--;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private void updateClientsList(String strFromServer) {
        String[] parts = strFromServer.split("\\s");
        switch (parts[0]) {
//            case "/update":
//                Platform.runLater(() -> {
//                    for (int i = 0; i < nickListItems.size(); i++) {
//                        if (nickListItems.contains(parts[1])) {
//                            nickListItems.get(i).replace(parts[1], parts[2]);
//                        }
//                    }
//                    clientsList.getItems().clear();//
//                });
//                break;
            case "/clientConnected":
                Platform.runLater(() -> {
                    for (int i = 1; i < parts.length; i++) {
                        if (!nickListItems.contains(parts[i])) {
                            nickListItems.add(parts[i]);
                        }
                    }
                });
                break;
            case "/clientDisconnected":
                Platform.runLater(() -> {
                    nickListItems.retainAll(parts);
                    clientsList.getItems().retainAll(nickListItems);
                });
                break;
        }
        clientsList.setItems(nickListItems);
        clientsList.refresh();
    }
}

