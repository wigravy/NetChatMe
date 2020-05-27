package source;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;

public class Main extends Application {
    private static final String VERSION = "ver 0.1a";
    private static final String PROJECT_NAME = "NetChatMe";


    @Override
    public void start(Stage primaryStage) throws Exception{
        Parent root = FXMLLoader.load(getClass().getResource("MainScene.fxml"));
        primaryStage.getIcons().add(new Image("\\resources\\icon.png"));
        primaryStage.setTitle(PROJECT_NAME + " " + VERSION);
        primaryStage.setScene(new Scene(root, 300, 700));
        primaryStage.setMinHeight(700);
        primaryStage.setMinWidth(300);
        primaryStage.setResizable(false);
        primaryStage.show();

    }


    public static void main(String[] args) {
        launch(args);
    }
}
