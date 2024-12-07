package client.scenes;

import commons.Note;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.scene.web.WebView;
import javafx.stage.Stage;
import java.net.URL;
import java.util.List;
import java.util.Optional;

public class MainCtrl {

    /**
     * The {@code selectedNoteIndex} variable tracks the index of the currently selected or
     * edited note in the {@code savedNotes} observable list.<br>
     * <br>
     * If {@code selectedNoteIndex == -1},  no note is being edited, and thus a new note
     * is ADDED<br>
     * If {@code selectedNoteIndex != -1}, an existing note is being updated, and the note at the
     *   {@code selectedNoteIndex} is modified ({@code savedNotes.set()}).<br>
     **/
    private int selectedNoteIndex  = -1;
    private boolean isDarkTheme = false;
    private String content = "";
    private ObservableList<Note> savedNotes = FXCollections.observableArrayList();
    private final MarkdownCtrl markdownCtrl = new MarkdownCtrl();

    private Stage primaryStage;
    private Scene mainView;

    @FXML
    private VBox leftPanel;

    @FXML
    private TextField searchBar;

    @FXML
    private Button searchButton;

    @FXML
    private ListView<Note> notesList;

    @FXML
    private Button addButton;

    @FXML
    private Button deleteButton;

    @FXML
    private Button refreshButton;

    @FXML
    private VBox middlePanel;

    @FXML
    private TextField noteTitleInput;

    @FXML
    private TextArea noteEditor;

    @FXML
    private VBox rightPanel;

    @FXML
    private Label renderedNoteTitleLabel;

    @FXML
    private MenuButton menuButton;

    @FXML
    private MenuItem themeToggleButton;

    @FXML
    private MenuItem hideEditorButton;

    @FXML
    private WebView renderedNoteView;

    @FXML
    private CheckBox problemsCheckbox;

    /**
     * Initializes the controller.
     *
     * @param primaryStage Reference to the primary stage.
     * @param mainView Holds a reference to the mainView scene.
     */
    public void initialize(Stage primaryStage, Scene mainView) {

        this.primaryStage = primaryStage;
        this.mainView = mainView;

        notesList.setItems(savedNotes);

        // This part was created with the help of generative AI,
        // it makes only the Title of the note appear in ListView
        notesList.setCellFactory(_ -> new ListCell<>() {
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

        notesList
                .getSelectionModel()
                .selectedItemProperty()
                .addListener((_, _, selectedNote) -> {
                    selectedNoteIndex = notesList.getSelectionModel().getSelectedIndex();
                    if (selectedNote != null) {
                        System.out.println("The selected note is on index:" + selectedNoteIndex);
                        noteTitleInput.setText(selectedNote.getTitle());
                        noteEditor.setText(selectedNote.getText());
                    }
                });

        noteTitleInput.textProperty().addListener((observable, oldValue, newValue) -> {
            updateTitle();
        });
        updateDisplay();

    }

    /**
     * Getter for the list of saved notes.
     * @return The list of saved notes.
     */
    public ObservableList<Note> getSavedNotes() {
        return savedNotes;
    }

    /**
     * Setter for the list of saved notes.
     * @param newNotes The new list of saved notes.
     */
    public void setSavedNotes(ObservableList<Note> newNotes) {
        this.savedNotes = newNotes;
    }

    /**
     * Getter for the note editor.
     * @return An instance of the note editor textarea.
     */
    public TextArea getNoteEditor() {
        return noteEditor;
    }

    /**
     * Getter for the note title input box.
     * @return An instance of the note title input box.
     */
    public TextField getNoteTitleInput() {
        return noteTitleInput;
    }

    /**
     * Getter for the rendered note webview.
     * @return An instance of the rendered note webview.
     */
    public WebView getRenderedNoteView() {
        return renderedNoteView;
    }

    /**
     * Getter for the rendered note title label.
     * @return An instance of the rendered note title label.
     */
    public Label getRenderedNoteTitleLabel() {
        return renderedNoteTitleLabel;
    }

    /**
     * Toggles the dark mode theme on/off depending on the current theme.
     */
    public void toggleTheme() {
        URL darkThemeCss = getClass().getResource("/client/scenes/dark-theme.css");
        URL lightThemeCss = getClass().getResource("/client/scenes/gui.css");
        if (darkThemeCss == null || lightThemeCss == null) return; //TODO handle null

        String oldForm = isDarkTheme ?
                darkThemeCss.toExternalForm() :
                lightThemeCss.toExternalForm();
        String newForm = isDarkTheme ?
                lightThemeCss.toExternalForm() :
                darkThemeCss.toExternalForm();

        mainView.getStylesheets().remove(oldForm);
        mainView.getStylesheets().add(newForm);

        if (isDarkTheme) {
            themeToggleButton.setText("🌙");
        } else {
            themeToggleButton.setText("☀");
            themeToggleButton.setStyle("-fx-font-size: 10px;");
        }

        isDarkTheme = !isDarkTheme;
    }

    /**
     * Opens a dialog to add a new note.
     * This method shows a small input dialog asking the user to enter a note title.
     * If the title is valid (not empty), it adds the title to the list of notes.
     * If the title is empty, an error message is displayed.
     */
    public void handleAddNote() {

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
                Note newNote = new Note(title.trim(), "");
                savedNotes.add(newNote);
                notesList.getSelectionModel().select(savedNotes.size() - 1);
                noteTitleInput.setText(newNote.getTitle());
                noteEditor.setText(newNote.getText());

                // Default message for empty note text
                renderedNoteView.getEngine().loadContent("The note is empty");
                selectedNoteIndex = savedNotes.size() - 1;

                noteEditor.requestFocus();
            }
        });
    }

    /**
     * Deletes a note from the list of notes.
     */
    public void handleDeleteNote() {
        if (selectedNoteIndex == -1) {
            showWarning("No Note Selected", "No note selected for deletion. Please select a note and try again.");
            return;
        }

        if (showConfirmation("Delete Note", "Are you sure you want to delete this note?")) {
            commitDeleteNote(selectedNoteIndex);
        } else {
            updateDisplay();
        }
    }


    /**
     * A Warning pop-up message (only option is to say OK)
     * A Confirmation pop=up message (Options: OK / CANCEL)
     */
    private void showWarning(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private boolean showConfirmation(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);

        Optional<ButtonType> result = alert.showAndWait();
        return result.isPresent() && result.get() == ButtonType.OK;
    }

    /**
     * Modifies the note list by deleting the note at a given index.
     *
     * @param noteIndex The index (in the note list) of the note to be deleted.
     */

    public void commitDeleteNote(int noteIndex) {
        savedNotes.remove(noteIndex);
        if (!savedNotes.isEmpty()) {
                int newIndex = Math.min(noteIndex, savedNotes.size() - 1);
                notesList.getSelectionModel().select(newIndex);
        } else {
            notesList.getSelectionModel().clearSelection();
        }
        displaySavedNote();
    }


    /**
     * Updates the contents of the editor and the rendered view
     * after the user select a different note to load.
     */
    public void displaySavedNote() {
        // If there is a valid index (not -1) and the index is within the range of savedNotes
        if (selectedNoteIndex != -1 && selectedNoteIndex < savedNotes.size()) {
            Note selectedNote = savedNotes.get(selectedNoteIndex);
            noteTitleInput.setText(selectedNote.getTitle());
            noteEditor.setText(selectedNote.getText());
            renderedNoteTitleLabel.setText(selectedNote.getTitle());
            renderedNoteView.getEngine().loadContent(selectedNote.getText());
        } else {
            // Reset when no valid note is selected (i.e., when the list is empty or nothing is selected)
            selectedNoteIndex = -1;
            noteTitleInput.clear();
            noteEditor.clear();
            renderedNoteTitleLabel.setText("The title is empty");
            renderedNoteView.getEngine().loadContent("The note is empty");
        }
    }

    /**
     * Adds a listener to the text editor to listen to changes made in the text,
     * so that they are automatically propagated in the rendered view.
     */
    public void updateDisplay() {
        noteEditor.textProperty().addListener((_, _, newValue) -> {
            content = newValue;
            renderedNoteView.getEngine().loadContent(markdownCtrl.markdownToHtml(content));
        });

        if (selectedNoteIndex != -1) {
            // If a note is selected, update the content of that note
            Note selectedNote = savedNotes.get(selectedNoteIndex);
            selectedNote.setText(content);
            notesList.refresh();
        } else {
            renderedNoteTitleLabel.setText("The title is empty");
            renderedNoteView.getEngine().loadContent("The note is empty");
        }
    }


    /**
     * Fetches new data from the server
     */
    public void handleRefreshNote() {
        // TODO
    }


    /**
     * Provides the rendered view when the "Render!" button is pressed.
     */
    public void renderNote() {
        String title = noteTitleInput.getText().trim();
        String note = noteEditor.getText().trim();

        updateTitle(); // This will update the title in the rendered view and the list.

        // If both the title and note are empty, set default content in the rendered view
        if (title.isEmpty() && note.isEmpty()) {
            renderedNoteView.getEngine().loadContent("The note is empty");
            return;
        }

        // Handle rendering note content (markdown processing or default message)
        if (note.isEmpty()) {
            renderedNoteView.getEngine().loadContent("The note is empty");
        } else {
            renderedNoteView.getEngine().loadContent(note);
        }

        // Handle adding/updating note in the saved notes list
        if (selectedNoteIndex == -1) {
            Note newNote = new Note(title, note);
            savedNotes.add(newNote);
            selectedNoteIndex = savedNotes.size() - 1;
            notesList.getSelectionModel().select(selectedNoteIndex);
        } else {
            Note selectedNote = savedNotes.get(selectedNoteIndex);
            selectedNote.setTitle(title);
            selectedNote.setText(note);
            notesList.refresh();
        }
    }


    /**
     * Displays the problems with Markdown rendering in the webview.
     */
    public void showProblems() {
        String renderedContent = markdownCtrl.markdownToHtml(content);
        if(problemsCheckbox.isSelected()){
            noteEditor.setEditable(false);
            List<String> problems = markdownCtrl.getProblems(content);
            StringBuilder result = new StringBuilder();
            for(int i = 0; i < problems.size(); i++){
                result.append(problems.get(i));
                if(i < problems.size() - 1)
                    result.append("\n");
            }
            renderedNoteView.getEngine().loadContent(result.toString());
        }
        else{
            noteEditor.setEditable(true);
            renderedNoteView.getEngine().loadContent(renderedContent);
        }
    }

    /**
     * Toggles the middle panel of the interface.
     */

    public void toggleEditor() {
        middlePanel.setVisible(!middlePanel.isVisible());
    }



    /**
     * Updates the title of the selected note with the text in the inputTitle field.<br>
     * <br>
     * This method is triggered whenever the user types or modifies the text
     * in the inputTitle TextField.<br>
     * <br>
     * It checks if a note is currently selected in the ListView. If a note is selected,
     * the title of the note is updated with the text entered by the user
     * in the inputTitle field.<br>
     */
    public void updateTitle() {
        String updatedTitle = noteTitleInput.getText();

        if (updatedTitle.isEmpty()) {
            renderedNoteTitleLabel.setText("Untitled Note");
        } else {
            renderedNoteTitleLabel.setText(updatedTitle);
        }

        Note selectedNote = notesList.getSelectionModel().getSelectedItem();

        if (selectedNote != null) {
            selectedNote.setTitle(updatedTitle);
            notesList.refresh();
        } else if (selectedNoteIndex == -1 && savedNotes.isEmpty()) {
            String noteContent = noteEditor.getText();
            Note firstNote = new Note(updatedTitle, noteContent);
            savedNotes.add(firstNote);
            selectedNoteIndex = 0;
            notesList.getSelectionModel().select(0);
            notesList.refresh();
        }
    }
}