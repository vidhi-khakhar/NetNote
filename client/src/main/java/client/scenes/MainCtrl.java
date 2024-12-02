package client.scenes;

import commons.Note;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.web.WebView;
import javafx.stage.Stage;

import java.util.List;
import java.util.Optional;

public class MainCtrl {

    /**
     * The {@code selectedNoteIndex} variable tracks the index of the currently selected or
     * edited note in the {@code savedNotes} list.<br>
     * <br>
     * When rendering a note (i.e., displaying its title and content in the display fields):<br>
     * If {@code selectedNoteIndex == -1},  no note is being edited, and thus a new note
     * is ADDED<br>
     * If {@code selectedNoteIndex != -1}, an existing note is being updated, and the note at the
     *   {@code selectedNoteIndex} is modified ({@code savedNotes.set()}).<br>
     * <br>
     * When deleting a note:<br>
     * The selected index is used to find the note to be removed from the list.<br>
     * The note at that index gets removed from the list
     *   ({@code savedNotes.remove()}).<br>
     * <br>
     * The {@code selectedNoteIndex} is reset to {@code -1} when no note is selected.
     **/
    private int selectedNoteIndex  = -1; // Index of the note being edited
                                         // (-1 means no note is selected).
    private boolean isDarkTheme = false; // Track the current theme
    private String content = "";
    private final MarkdownCtrl markdownCtrl = new MarkdownCtrl();

    private Stage primaryStage;
    private Scene mainView;

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

    @FXML
    private Button addButton;

    @FXML
    private Button deleteButton;

    @FXML
    private Button refreshButton;

    @FXML
    private VBox middlePanel;

    @FXML
    private Label noteEditorLabel;

    @FXML
    private TextArea noteEditor;

    @FXML
    private TextField noteTitleInput;

    @FXML
    private Button renderButton;

    // RIGHT PANEL ELEMENTS
    @FXML
    private VBox rightPanel;

    @FXML
    private Button themeToggleButton;

    @FXML
    private WebView renderedNoteView;

    @FXML
    private Label renderedNoteTitleLabel;

    @FXML
    private Button hideEditorButton;

    @FXML
    private CheckBox problemsCheckbox;

    private ObservableList<Note> savedNotes = FXCollections.observableArrayList();

    /**
     * Initializes the controller.
     *
     * @param primaryStage Reference to the primary stage.
     * @param mainView Holds a reference to the mainView scene.
     */
    public void initialize(Stage primaryStage, Scene mainView) {

        this.mainView = mainView;

        notesList.setItems(savedNotes);

        // This part was created with the help of generative AI,
        // it makes only the Title of the note appear in ListView
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

        notesList
                .getSelectionModel()
                .selectedItemProperty()
                .addListener((obs, oldNote, selectedNote) -> {
                    if (selectedNote != null) {

                        // Change title in the editor
                        noteTitleInput.setText(selectedNote.getTitle());

                        // Change text in the editor
                        noteEditor.setText(selectedNote.getText());
                    }
                });
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
        if (isDarkTheme) {
            // Switch to light theme
            mainView.getStylesheets()
                    .remove(getClass()
                            .getResource("/client/scenes/dark-theme.css")
                            .toExternalForm());
            mainView.getStylesheets()
                    .add(getClass()
                            .getResource("/client/scenes/gui.css")
                            .toExternalForm());
            themeToggleButton.setText("🌙");
        } else {
            // Switch to dark theme
            mainView.getStylesheets()
                    .remove(getClass()
                            .getResource("/client/scenes/gui.css")
                            .toExternalForm());
            mainView.getStylesheets()
                    .add(getClass()
                            .getResource("/client/scenes/dark-theme.css")
                            .toExternalForm());

            // Set the sun icon and adjust font size
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
     *
     * @param event the action event triggered by clicking the "+" button
     */
    @SuppressWarnings("unused")
    public void handleAddNote(ActionEvent event) {

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

                // Create a new Note object with default empty text
                Note newNote = new Note(title.trim(), "");

                // Add the new note to the savedNotes list
                savedNotes.add(newNote);

                // Select the new note in the ListView
                notesList.getSelectionModel().select(savedNotes.size() - 1);

                // Set the title in editor
                noteTitleInput.setText(newNote.getTitle());

                // Set the note text in editor
                noteEditor.setText(newNote.getText());

                // Default message for empty note text
                renderedNoteView.getEngine().loadContent("The note is empty");

                selectedNoteIndex = savedNotes.size() - 1;
            }
        });
    }

    /**
     * Deletes a note from the list of notes.
     *
     * @param event Reference to the action event.
     */
    @SuppressWarnings("unused")
    public void handleDeleteNote(ActionEvent event) {
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
                noteTitleInput.clear();
                noteEditor.clear();
                renderedNoteTitleLabel.setText("The title is empty");
                renderedNoteView.getEngine().loadContent("The note is empty");
                selectedNoteIndex = -1; // No note selected after deletion
            }
        } else {
            System.out.println("No note selected for deletion");
        }
    }

    /**
     * Fetches new data from the server
     *
     * @param event Reference to the action event
     */
    @SuppressWarnings("unused")
    public void handleRefreshNote(ActionEvent event) {
        // TODO
    }

    /**
     * Updates the contents of the editor and the rendered view
     * after the user select a different note to load.
     *
     * @param event Reference to the mouse click event.
     */
    @SuppressWarnings("unused")
    public void displaySavedNote(MouseEvent event) {
        int index = notesList.getSelectionModel().getSelectedIndex();
        if (index != -1) {
            Note selectedNote = savedNotes.get(index);
            selectedNoteIndex = index;

            noteTitleInput.setText(selectedNote.getTitle());
            noteEditor.setText(selectedNote.getText());

            renderedNoteTitleLabel.setText(selectedNote.getTitle());
            renderedNoteView.getEngine().loadContent(selectedNote.getText());
        }
    }

    /**
     * Provides the rendered view when the "Render!" button is pressed.
     * @param event Reference to the mouse click event.
     */
    @SuppressWarnings("unused")
    public void renderNote(MouseEvent event) {
        String title = noteTitleInput.getText().trim();
        String note = noteEditor.getText().trim();

        // Check if inputs are empty and display appropriate messages
        if (title.isEmpty() && note.isEmpty()) {
            renderedNoteTitleLabel.setText("The title is empty");
            renderedNoteView.getEngine().loadContent("The note is empty");
            return;
        } else {
            // If the title is empty, set a default title
            if (title.isEmpty()) {
                title = "Untitled Note";  // Default title
                renderedNoteTitleLabel.setText(title);
            } else {
                renderedNoteTitleLabel.setText(title);
            }

            if (note.isEmpty()) {
                renderedNoteView.getEngine().loadContent("The note is empty");
            } else {
                renderedNoteView.getEngine().loadContent(note);
            }
        }

        // If no note is being updated (new note)
        if (selectedNoteIndex == -1) {
            Note savedNote = new Note(title, note);
            savedNotes.add(savedNote); // Add to the ObservableList
            selectedNoteIndex = savedNotes.size() - 1; // Set the index of the newly added note
        } else {
            // Update the existing note
            Note updatedNote = new Note(title, note);
            savedNotes.set(selectedNoteIndex, updatedNote); // Update the note in the list
        }
    }

    /**
     * Adds a listener to the text editor to listen to changes made in the text,
     * so that they are automatically propagated in the rendered view.
     *
     * @param event Reference to the key press event
     */
    @SuppressWarnings("unused")
    public void updateDisplay(KeyEvent event) {
        noteEditor.textProperty().addListener((_, _, newValue) -> {
            content = newValue;
            renderedNoteView.getEngine().loadContent(markdownCtrl.markdownToHtml(content));
        });
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
     *
     * @param event Reference to the mouse click event
     */
    @SuppressWarnings("unused")
    public void toggleEditor(MouseEvent event) {
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
     *
     * @param event it is triggered when the user types in the inputTitle field.
     *              This method listens to key releases in the TextField.
     */
    @SuppressWarnings("unused")
    public void updateTitle(KeyEvent event) {
        String updatedTitle = noteTitleInput.getText();
        Note selectedNote = notesList.getSelectionModel().getSelectedItem();
            //common scenario:
        if (selectedNote != null) {
            selectedNote.setTitle(updatedTitle);
            notesList.refresh();
            //scenario where no note is selected:
        } else if (selectedNoteIndex == -1 && !savedNotes.isEmpty()) {
            Note newNote = savedNotes.getFirst();
            newNote.setTitle(updatedTitle);
            selectedNoteIndex = 0;
            notesList.getSelectionModel().select(0);
            notesList.refresh();
            //scenario where there is no note:
        } else if (savedNotes.isEmpty()) {
            Note firstNote = new Note(updatedTitle, "");
            savedNotes.add(firstNote);
            selectedNoteIndex = 0;
            notesList.getSelectionModel().select(0);
            notesList.refresh();
        }
    }
}
