package chat;

import javafx.fxml.FXML;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyEvent;

public class Controller {

    @FXML
    private TextArea textArea;

    @FXML
    private TextField textField;

    public void addMessage() {
        writeMessage();
    }

    public void keyTyped(KeyEvent keyEvent) {
        if(keyEvent.getCharacter().equals("\r")) {
            writeMessage();
        }
    }

    private void writeMessage() {
        String message = textField.getText();
        if (message.length() > 0) {
            textArea.appendText(message + "\n");
            textField.clear();
        }
        textField.requestFocus();
    }
}
