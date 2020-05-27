package source;


import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Tooltip;
import javafx.scene.input.KeyCode;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;


public class Controller {


    @FXML
    private Button sendMessageButton;

    @FXML
    public javafx.scene.control.TextArea messages;

    @FXML
    public javafx.scene.control.TextArea writingTextArea;

    @FXML
    private void sendMessageOnClick(MouseEvent event) {
        if (event.getButton().equals(MouseButton.PRIMARY)) {
            sendMessage();
        }
    }

    @FXML
    private void sendMessageOnEnter(javafx.scene.input.KeyEvent keyEvent) {
        if ((keyEvent.getCode() == KeyCode.ENTER)) {
            sendMessage();
            keyEvent.consume();
        }
    }
    //TODO: проработать покраску сообщений в разные цвета. Создать цвета которые будет приятно видеть и давать пользователю случайный уникальный цвет.
    private void sendMessage() {
        if (!writingTextArea.getText().trim().isEmpty()) {
            messages.appendText(getCurrentTime() + " " + getUserName() + ": " + writingTextArea.getText().trim());
            messages.appendText("\n");
            writingTextArea.clear();
            writingTextArea.requestFocus();
        } else {
            writingTextArea.clear();
            writingTextArea.requestFocus();
        }
    }
    //TODO: найти более оптимальный способ получения времени.
    private String getCurrentTime() {
        Calendar calendar = Calendar.getInstance();
        Date date = calendar.getTime();
        DateFormat dateFormat = new SimpleDateFormat("HH:mm");
        return dateFormat.format(date);
    }

    //TODO: сделать выбор имени пользователя для чата.
    private String getUserName() {
        return "wigravy";
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
        sendMessageButton.setTooltip(new Tooltip("Отправить сообщение"));
    }
}
