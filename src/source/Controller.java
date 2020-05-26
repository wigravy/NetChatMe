package source;


import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.input.KeyCode;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;



public class Controller {
    @FXML
    private Button sendMSGButton;

    @FXML
    public javafx.scene.control.TextArea messages;

    @FXML
    public javafx.scene.control.TextArea writingTextArea;

    @FXML
    private void sendMessageOnClick(MouseEvent event) {
        if (event.getButton().equals(MouseButton.PRIMARY) && !writingTextArea.getText().trim().isEmpty()) {
            sendMessage();
        }
    }


    @FXML
    private void sendMessageOnEnter(javafx.scene.input.KeyEvent keyEvent) {
        if ((keyEvent.getCode() == KeyCode.ENTER) && (!writingTextArea.getText().trim().isEmpty())) {
           sendMessage();
        } else if ((keyEvent.getCode() == KeyCode.ENTER)){
            writingTextArea.clear();
        }
    }

    private void sendMessage() {
        messages.appendText(writingTextArea.getText());
        messages.appendText("\n");
        writingTextArea.clear();
        writingTextArea.requestFocus();
    }

    @FXML
    private void initialize() {
        messages.autosize();
        messages.setEditable(false);
        messages.setFocusTraversable(false);
        messages.setWrapText(true);
        writingTextArea.setPromptText("Напишите сообщение...");
        writingTextArea.requestFocus();
        writingTextArea.setWrapText(true);

    }


}
