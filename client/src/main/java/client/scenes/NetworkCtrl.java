package client.scenes;

import javafx.fxml.FXML;
import javafx.scene.control.RadioButton;
import javafx.scene.control.ToggleGroup;

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
    }
}
