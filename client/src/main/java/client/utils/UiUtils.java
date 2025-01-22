package client.utils;

import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ChoiceDialog;
import javafx.scene.control.TextInputDialog;

import java.util.List;
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

    /**
     * Shows a choice dialog for the user to select an option from a dropdown list.
     *
     * @param title      The title of the dialog window
     * @param header     The header text of the dialog.
     * @param content    The text displayed in the dialog.
     * @param choices    The list of choices the user can select from.
     * @param defaultChoice The default choice to be preselected.
     * @return The user's selected choice wrapped in an {@link Optional}.
     */
    public Optional<String> showChoiceDialog
    (String title, String header, String content, List<String> choices, String defaultChoice) {
        ChoiceDialog<String> dialog = new ChoiceDialog<>(defaultChoice, choices);
        dialog.setTitle(title);
        dialog.setHeaderText(header);
        dialog.setContentText(content);
        return dialog.showAndWait();
    }
}
