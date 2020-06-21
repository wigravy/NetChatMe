package source.client;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

public class Main extends Application {
    private static final String VERSION = "ver 0.2a";
    private static final String PROJECT_NAME = "NetChatMe";
    private static Scene scene;

    public static DataInputStream getInputStream() {
        return inputStream;
    }

    public static DataOutputStream getOutputStream() {
        return outputStream;
    }

    public static Socket getSocket() {
        return socket;
    }

    private static DataInputStream inputStream;
    private static DataOutputStream outputStream;
    private static Socket socket = null;

    static void setRoot(String fxml) throws IOException {
        scene.setRoot(loadFXML(fxml));
    }

    private static Parent loadFXML(String fxml) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(Main.class.getResource(fxml + ".fxml"));
        return fxmlLoader.load();
    }

    @Override
    public void start(Stage primaryStage) throws Exception{
        Parent root = FXMLLoader.load(getClass().getResource("Authorization.fxml"));
        scene = new Scene(root, 500, 700);
        primaryStage.getIcons().add(new Image("resources/icon.png"));
        primaryStage.setTitle(PROJECT_NAME + " " + VERSION);
        primaryStage.setScene(scene);
        primaryStage.setMinHeight(700);
        primaryStage.setMinWidth(500);
        primaryStage.setResizable(false);
        primaryStage.show();

        primaryStage.setOnCloseRequest(windowEvent -> {
            try {
                outputStream.writeUTF("/end");
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
    }

    public static void main(String[] args) {
        try {
            socket = new Socket("localhost", 8030);
            inputStream = new DataInputStream(socket.getInputStream());
            outputStream = new DataOutputStream(socket.getOutputStream());
        } catch (IOException e) {
            System.out.println("Connection error");
        }
        launch(args);
    }
}
