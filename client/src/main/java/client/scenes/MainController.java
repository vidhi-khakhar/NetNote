package client.scenes;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

public class MainController {

    @FXML
    private Button addButton;

    @FXML
    private Button deleteButton;

    @FXML
    private Label displayTitle;

    @FXML
    private TextArea inputNote;

    @FXML
    private VBox leftPanel;

    @FXML
    private VBox middlePanel;

    @FXML
    private Label noteEditor;

    @FXML
    private ListView<?> notesList;

    @FXML
    private Button refreshButton;

    @FXML
    private VBox rightPanel;

    @FXML
    private HBox rootContainer;

    @FXML
    private Button searchButton;

    @FXML
    private TextField titleNote;

    @FXML
    void handleAddNote(ActionEvent event) {

    }

    @FXML
    void handleDeleteNote(ActionEvent event) {

    }

    @FXML
    void handleRefresh(ActionEvent event) {

    }

}
