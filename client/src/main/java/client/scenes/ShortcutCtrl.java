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

    @FXML
    private Label moveNoteShortcut;

    @FXML
    private Label boldTextShortcut;

    @FXML
    private Label italicTextShortcut;

    @FXML
    private Label underlineTextShortcut;

    @FXML
    private Label cycleLanguageShortcut;

    @FXML
    private Label cycleLanguageShortcutReverse;

    @FXML
    private Label editCollectionsShortcut;

    @FXML
    private Label toggleProblemsShortcut;

    @FXML
    private Label focusNoteListShortcut;

    @FXML
    private Label focusFileListShortcut;

    @FXML
    private Label focusNoteEditorShortcut;

    @FXML
    private Label focusCollectionDropdownShortcut;

    @FXML
    private Label addCollectionShortcut;

    @FXML
    private Label removeCollectionShortcut;

    @FXML
    private Label saveShortcut;

    @FXML
    private Label focusCollectionListShortcut;

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
        moveNoteShortcut.textProperty().bind(props.get("moveNoteShortcut"));
        boldTextShortcut.textProperty().bind(props.get("boldTextShortcut"));
        italicTextShortcut.textProperty().bind(props.get("italicTextShortcut"));
        underlineTextShortcut.textProperty().bind(props.get("underlineTextShortcut"));
        cycleLanguageShortcut.textProperty().bind(props.get("cycleLanguageShortcut"));
        cycleLanguageShortcutReverse.textProperty().bind(props.get("cycleLanguageShortcutReverse"));
        editCollectionsShortcut.textProperty().bind(props.get("editCollectionsShortcut"));
        toggleProblemsShortcut.textProperty().bind(props.get("toggleProblemsShortcut"));
        focusNoteListShortcut.textProperty().bind(props.get("focusNoteListShortcut"));
        focusFileListShortcut.textProperty().bind(props.get("focusFileListShortcut"));
        focusNoteEditorShortcut.textProperty().bind(props.get("focusNoteEditorShortcut"));
        focusCollectionDropdownShortcut.textProperty().bind(
                props.get("focusCollectionDropdownShortcut"));
        addCollectionShortcut.textProperty().bind(props.get("addCollectionShortcut"));
        removeCollectionShortcut.textProperty().bind(props.get("removeCollectionShortcut"));
        saveShortcut.textProperty().bind(props.get("saveShortcut"));
        focusCollectionListShortcut.textProperty().bind(props.get("focusCollectionListShortcut"));
    }

    /**
     * Closes the pop-up using the OK button.
     *
     * @param event The event that triggered the action.
     */
    public void closeShortcutOverview(ActionEvent event) {
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.close();
    }
}