package client.scenes;

import commons.Note;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

import java.util.Optional;

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
    private ListView<Note> notesList;
    private ObservableList<Note> savedNotes = FXCollections.observableArrayList(); // To store saved notes.

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
    public void initialize() {

        notesList.setItems(savedNotes);


        // This part was created with the help of generative AI, it makes only the Title of the note appear in ListView
        notesList.setCellFactory(listView -> new ListCell<>() {
            @Override
            protected void updateItem(Note note, boolean empty) {
                super.updateItem(note, empty);
                if (empty || note == null) {
                    setText(null);
                } else {
                    setText(note.getTitle());
                }
            }
        });

        notesList.getSelectionModel().selectedItemProperty().addListener((obs, oldNote, selectedNote) -> {
            if (selectedNote != null) {
                inputTitle.setText(selectedNote.getTitle()); // Change title in the editor
                inputNote.setText(selectedNote.getText());  // Change text in the editor
            }
        });
    }

    /**
     * JAVADOC:
     * The `selectedNoteIndex` variable trackS the index of the currently selected or edited note
     * in the `savedNotes` list.

     * When rendering a note (i.e., displaying its title and content in the display fields):
     * If `selectedNoteIndex == -1`,  no note is being edited, and thus a new note is ADDED
     * If `selectedNoteIndex != -1`, an existing note is being updated, and the note at the
     *   `selectedNoteIndex` is modified (`savedNotes.set()`).

     * When deleting a note:
     * The selected index is used to find the note to be removed from the list. The note at that index is removed from the list
     *   (`savedNotes.remove()`).

     * The `selectedNoteIndex` is reset to `-1` when no note is selected.
     * **/

    /**
     * Opens a dialog to add a new note.
     * This method shows a small input dialog asking the user to enter a note title.
     * If the title is valid (not empty), it adds the title to the list of notes.
     * If the title is empty, an error message is displayed.
     *
     * @param event the action event triggered by clicking the "+" button
     */
    @FXML
    void handleAddNote(ActionEvent event) {

        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("Add Note");
        dialog.setHeaderText(null);
        dialog.setContentText("Please enter the title of your note:");

        Optional<String> result = dialog.showAndWait();
        result.ifPresent(title -> {
            if (title.trim().isEmpty()) {
                // Handling the invalid inputs
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Invalid Title");
                alert.setHeaderText(null);
                alert.setContentText("The title cannot be empty!");
                alert.showAndWait();
            } else {
                // Create a new Note object
                Note newNote = new Note();
                newNote.setTitle(title.trim());
                newNote.setText(""); // Default empty text

                savedNotes.add(newNote);     // Add the new note to the savedNotes list

                notesList.getSelectionModel().select(savedNotes.size() - 1);  // Select the new note in the ListView

                inputTitle.setText(newNote.getTitle()); //Set the title in editor
                inputNote.setText(newNote.getText());   //Set the note text in editor
                displayNote.setText("The note is empty"); // Default message for empty note text

                selectedNoteIndex = savedNotes.size() - 1;
            }
        });
    }

    @FXML
    void handleDeleteNote(ActionEvent event) {
        int index = notesList.getSelectionModel().getSelectedIndex();

        if (index != -1) {
            // Show confirmation dialog before deleting
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("Delete Note");
            alert.setHeaderText(null);
            alert.setContentText("Are you sure you want to delete this note?");

            // Show the dialog and wait for the response
            Optional<ButtonType> result = alert.showAndWait();

            if (result.isPresent() && result.get() == ButtonType.OK) {
                // If the user confirms, delete the note
                savedNotes.remove(index);
                inputTitle.clear();
                inputNote.clear();
                displayTitle.setText("The title is empty");
                displayNote.setText("The note is empty");
                selectedNoteIndex = -1; // No note selected after deletion
            }
        } else {
            System.out.println("No note selected for deletion");
        }
    }

    @FXML
    void handleRefresh(ActionEvent event) {

    }

    @FXML
    void displaySavedNote(MouseEvent event) {
        int index = notesList.getSelectionModel().getSelectedIndex();
        if (index != -1) {
            Note selectedNote = savedNotes.get(index);
            selectedNoteIndex = index;

            inputTitle.setText(selectedNote.getTitle());
            inputNote.setText(selectedNote.getText());

            displayTitle.setText(selectedNote.getTitle());
            displayNote.setText(selectedNote.getText());
        }
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
            // If the title is empty, set a default title
            if (title.isEmpty()) {
                title = "Untitled Note";  // Default title
                displayTitle.setText(title);
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
            Note savedNote = new Note();
            savedNote.setTitle(title);
            savedNote.setText(note);
            savedNotes.add(savedNote); // Add to the ObservableList
            selectedNoteIndex = savedNotes.size() - 1; // Set the index of the newly added note
        } else {
            // Update the existing note
            Note updatedNote = new Note();
            updatedNote.setTitle(title);
            updatedNote.setText(note);
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
    void updateTitle(KeyEvent event) {
        Note selectedNote = notesList.getSelectionModel().getSelectedItem();

        if (selectedNote != null) {
            selectedNote.setTitle(inputTitle.getText());
            notesList.refresh();
        }
    }

}


