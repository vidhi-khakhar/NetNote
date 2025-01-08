package client.utils;

import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TextInputDialog;

import java.util.Optional;

public class UiUtils {

    /**
     * A Warning pop-up message (only option is to say OK)
     *
     * @param title   The title of the pop-up message
     * @param message The body of the pop-up message
     * @param type    The type of the alert object
     * @return True if the user presses 'OK', false otherwise
     */
    public boolean showPopUp(String title, String message, Alert.AlertType type) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);

        Optional<ButtonType> result = alert.showAndWait();
        return result.isPresent() && result.get() == ButtonType.OK;
    }

    /**
     * Shows a dialog that asks the user to write something in a
     * text box.
     *
     * @param title   The title of the dialog window
     * @param content The text displayed in the dialog
     * @param header  The header text of the dialog
     * @return What the user entered in the text input, wrapped inside
     * an {@link Optional}
     */
    public Optional<String> askUserForInput(String title, String content, String header) {
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle(title);
        dialog.setContentText(content);
        dialog.setHeaderText(header);
        return dialog.showAndWait();
    }
}
