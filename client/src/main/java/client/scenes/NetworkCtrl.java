package client.scenes;

import client.utils.note.NoteServerUtils;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.RadioButton;
import javafx.scene.control.ToggleGroup;
import javafx.scene.input.Clipboard;
import javafx.scene.input.ClipboardContent;
import javafx.scene.text.Text;

/**
 * Controller for the Network menu
 * Methods starting with "handle" are directly referenced in the FXML code
 */
public class NetworkCtrl {

    @FXML
    private RadioButton radioNotes;

    @FXML
    private RadioButton radioCollections;

    @FXML
    private RadioButton radioFiles;

    @FXML
    private Button copyButton;

    @FXML
    private Text localAddressText;

    /**
     * Initializes the {@link NetworkCtrl} controller by
     * binding the appropriate text properties and adding the
     * appropriate event listeners.
     */
    public void initialize() {
        ToggleGroup radioGroup = new ToggleGroup();
        radioNotes.setToggleGroup(radioGroup);
        radioCollections.setToggleGroup(radioGroup);
        radioFiles.setToggleGroup(radioGroup);

        localAddressText.setText(NoteServerUtils.getLocalAddress());
    }

    /**
     * Copies the address of the local server in the current network
     * to the system clipboard. <br>
     * Changes the text of the button to "Copied!" after the copy operation,
     * then back to "Copy" after 2 seconds.
     */
    public void copyLocalAddressToClipboard() {
        Clipboard clipboard = Clipboard.getSystemClipboard();
        ClipboardContent content = new ClipboardContent();
        content.putString(NoteServerUtils.getLocalAddress());
        clipboard.setContent(content);
        copyButton.setText("Copied!");
        Thread.ofPlatform().start(() -> {
            try {
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                System.out.println("Thread was interrupted");
            }
            // All UI changes must be performed by the main application thread
            Platform.runLater(() -> copyButton.setText("Copy"));
        });
    }
}
