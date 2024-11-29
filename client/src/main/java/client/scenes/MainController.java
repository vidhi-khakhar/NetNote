package client.scenes;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

public class MainController {

    private int selectedNoteIndex  = -1; // Index of the note being edited (-1 means no note is selected).

    @FXML
    private HBox rootContainer;


    // LEFT PANEL ELEMENTS
    @FXML
    private VBox leftPanel;

    // ADD SEARCH ELEMENTS
    @FXML
    private Button searchButton;

    @FXML
    private ListView<String> notesList;
    private ObservableList<String> savedNotes = FXCollections.observableArrayList(); // To store saved notes.

    @FXML
    private Button addButton;

    @FXML
    private Button deleteButton;

    @FXML
    private Button refreshButton;


    // MIDDLE PANEL ELEMENTS
    @FXML
    private VBox middlePanel;

    @FXML
    private Label noteEditor;

    @FXML
    private TextArea inputNote;

    public TextArea getInputNote() {
        return inputNote;
    }

    public void setInputNote(TextArea inputNote) {
        this.inputNote = inputNote;
    }

    @FXML
    private TextField inputTitle;

    public TextField getInputTitle() {
        return inputTitle;
    }

    public void setInputTitle(TextField inputTitle) {
        this.inputTitle = inputTitle;
    }


    @FXML
    private Button renderButton;


    // RIGHT PANEL ELEMENTS
    @FXML
    private VBox rightPanel;

    @FXML
    private Label displayNote;

    public Label getDisplayNote() {
        return displayNote;
    }

    public void setDisplayNote(Label displayNote) {
        this.displayNote = displayNote;
    }

    @FXML
    private Label displayTitle;
    public Label getDisplayTitle() {
        return displayTitle;
    }

    public void setDisplayTitle(Label displayTitle) {
        this.displayTitle = displayTitle;
    }

    @FXML
    private Button hideEditorButton;


    // METHOD IMPLEMENTATIONS
    @FXML
    void handleAddNote(ActionEvent event) {

    }

    @FXML
    void handleDeleteNote(ActionEvent event) {

    }

    @FXML
    void handleRefresh(ActionEvent event) {

    }

    @FXML
    void renderNote(MouseEvent event) {
        String title = inputTitle.getText().trim();
        String note = inputNote.getText().trim();

        // Check if inputs are empty and display appropriate messages
        if (title.isEmpty() && note.isEmpty()) {
            displayTitle.setText("The title is empty");
            displayNote.setText("The note is empty");
            return;
        } else {
            if (title.isEmpty()) {
                displayTitle.setText("The title is empty");
            } else {
                displayTitle.setText(title);
            }

            if (note.isEmpty()) {
                displayNote.setText("The note is empty");
            } else {
                displayNote.setText(note);
            }
        }

        // If no note is being updated (new note)
        if (selectedNoteIndex == -1) {
            String savedNote = title;
            savedNotes.add(savedNote); // Add to the ObservableList
            selectedNoteIndex = savedNotes.size() - 1; // Set the index of the newly added note
        } else {
            // Update the existing note
            String updatedNote = title;
            savedNotes.set(selectedNoteIndex, updatedNote); // Update the note in the list
        }
    }

    @FXML
    void updateDisplay(KeyEvent event) {

    }

    @FXML
    void hideEditor(MouseEvent event) {
        if (middlePanel.isVisible()) {
            // Hide the middle panel
            middlePanel.setVisible(false);

        } else {
            // Show the middle panel again
            middlePanel.setVisible(true);
        }
    }

    @FXML
    public void initialize() {
        notesList.setItems(savedNotes); // Bind the list to the ListView.
    }
}


