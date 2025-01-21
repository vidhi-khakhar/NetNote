package client.scenes;


import com.google.inject.Inject;
import javafx.beans.property.StringProperty;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import client.utils.LangUtils;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.stage.Stage;


import java.util.Map;


/**
 * Controller for the Shortcuts Overview
 */
public class ShortcutCtrl {
    @Inject
    private LangUtils langUtils;


    private Map<String, StringProperty> props;


    @FXML
    private Label addShortcut;


    @FXML
    private Label cycleShortcut;


    @FXML
    private Label deleteShortcut;


    @FXML
    private Label keyboardShortcut;


    @FXML
    private Label redoShortcut;


    @FXML
    private Label refreshShortcut;


    @FXML
    private Label searchShortcut;


    @FXML
    private Label undoShortcut;


    /**
     * This initialises the controller.
     */
    public void initialize() {
        props = langUtils.getProps();
        initializeTextProperties();
    }


    /**
     * This simply allows for internationalization.
     */
    private void initializeTextProperties() {
        keyboardShortcut.textProperty().bind(props.get("keyboardShortcutName"));
        searchShortcut.textProperty().bind(props.get("searchShortcutName"));
        addShortcut.textProperty().bind(props.get("addShortcutName"));
        deleteShortcut.textProperty().bind(props.get("deleteShortcutName"));
        refreshShortcut.textProperty().bind(props.get("refreshShortcutName"));
        undoShortcut.textProperty().bind(props.get("undoShortcutName"));
        redoShortcut.textProperty().bind(props.get("redoShortcutName"));
        cycleShortcut.textProperty().bind(props.get("cycleShortcutName"));
    }

    /**
     * Closes the pop-up using the OK button.
     * @param event
     */
    public void closeShortcutOverview(ActionEvent event) {
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.close();
    }
}