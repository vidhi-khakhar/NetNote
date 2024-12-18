package client.scenes;

import client.Main;
import client.UserConfigLoader;
import client.utils.undo.*;
import com.google.inject.Inject;
import commons.Note;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.beans.value.ChangeListener;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.VBox;
import javafx.scene.web.WebView;
import javafx.stage.Stage;

import java.net.URL;
import java.util.*;

import client.utils.NoteServerUtils;

public class MainCtrl {

    /**
     * The {@code selectedNoteIndex} variable tracks the index of the currently selected or
     * edited note in the {@code savedNotes} observable list.<br>
     * <br>
     * If {@code selectedNoteIndex == -1},  no note is being edited, and thus a new note
     * is ADDED<br>
     * If {@code selectedNoteIndex != -1}, an existing note is being updated, and the note at the
     * {@code selectedNoteIndex} is modified ({@code savedNotes.set()}).<br>
     **/
    private int selectedNoteIndex = -1;
    private boolean isDarkTheme = false;
    private String content = "";
    private ObservableList<Note> savedNotes = FXCollections.observableArrayList();

    private final StringProperty propertyEditorPlaceholder = new SimpleStringProperty();
    private final StringProperty propertyTitlePlaceholder = new SimpleStringProperty();
    private final StringProperty propertyNoteEditorName = new SimpleStringProperty();
    private final StringProperty propertyRenderedViewName = new SimpleStringProperty();
    private final StringProperty propertyMarkdownCheckboxLabel = new SimpleStringProperty();
    private final StringProperty propertySearchPlaceholder = new SimpleStringProperty();
    private final StringProperty propertyRenderedNoteTitleDefaultLabel = new SimpleStringProperty();
    private final StringProperty propertyRenderedNoteViewEmpty = new SimpleStringProperty();
    private final StringProperty propertyRenderedNoteTitleLabelUntitled =
            new SimpleStringProperty();
    private final StringProperty propertyAddDialogTitle = new SimpleStringProperty();
    private final StringProperty propertyAddDialogText = new SimpleStringProperty();
    private final StringProperty propertyInvalidTitleAlertTitle = new SimpleStringProperty();
    private final StringProperty propertyInvalidTitleAlertText = new SimpleStringProperty();
    private final StringProperty propertyDeleteNoteWarningTitle = new SimpleStringProperty();
    private final StringProperty propertyDeleteNoteWarningText = new SimpleStringProperty();
    private final StringProperty propertyDeleteNoteConfirmationTitle = new SimpleStringProperty();
    private final StringProperty propertyDeleteNoteConfirmationText = new SimpleStringProperty();
    private Scene mainView;

    @Inject
    private MarkdownCtrl markdownCtrl;

    @Inject
    private UndoHandler undoHandler;

    @Inject
    private NoteServerUtils noteServerUtils;

    @Inject
    private UserConfigLoader configLoader;

    @FXML
    private TextField searchBar;

    @FXML
    private ListView<Note> notesList;

    @FXML
    private VBox middlePanel;

    @FXML
    private TextField noteTitleInput;

    private final ChangeListener<String> noteTitleInputListener =
            (_, _, _) -> updateTitle();

    @FXML
    private TextArea noteEditor;

    @FXML
    private Label renderedNoteTitleLabel;

    @FXML
    private ImageView selectedLanguageImage;

    @FXML
    private MenuItem themeToggleButton;

    @FXML
    private WebView renderedNoteView;

    @FXML
    private CheckBox problemsCheckbox;

    @FXML
    private Label noteEditorLabel;

    @FXML
    private Label renderedNoteLabel;

    /**
     * Initializes the controller.
     *
     * @param stage    Reference to the primary stage.
     * @param mainView Holds a reference to the mainView scene.
     */
    public void initialize(Stage stage, Scene mainView) {

        this.mainView = mainView;

        savedNotes = FXCollections.observableArrayList(noteServerUtils.getAllNotes());
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

        noteTitleInput.textProperty().addListener(noteTitleInputListener);
        updateDisplay();


        mainView.addEventHandler(KeyEvent.KEY_PRESSED, event -> {
            List<String> eventKeys = new ArrayList<>();
            if (event.isControlDown()) eventKeys.add("Ctrl");
            if (event.isAltDown()) eventKeys.add("Alt");
            if (event.isShiftDown()) eventKeys.add("Shift");
            eventKeys.add(event.getCode().getChar());

            if (event.getCode() == KeyCode.ESCAPE) {
                searchBar.requestFocus(); // fixed indents.
                event.consume();
            }

            switch (String.join("+", eventKeys)) {
                case "Ctrl+Z" -> {
                    try {
                        undoHandler.undoAction();
                    } catch (CannotUndoException e) {
                        //TODO Show pop-up that the action could not be undone
                    }
                }
                case "Ctrl+Y" -> {
                    try {
                        undoHandler.redoAction();
                    } catch (CannotRedoException e) {
                        //TODO Show pop-up that the action could not be redone
                    }
                }
                case "Ctrl+N" -> handleAddNote();
                case "Ctrl+D" -> handleDeleteNote();
                case "Ctrl+B" -> boldMarkdown();
                case "Ctrl+I" -> italicMarkdown();
                case "Ctrl+U" -> underlineMarkdown();
            }
        });
        initializeTextProperties();
    }

    private void initializeTextProperties() {
        noteEditor.promptTextProperty().bind(propertyEditorPlaceholder);
        noteTitleInput.promptTextProperty().bind(propertyTitlePlaceholder);
        noteEditorLabel.textProperty().bind(propertyNoteEditorName);
        renderedNoteLabel.textProperty().bind(propertyRenderedViewName);
        problemsCheckbox.textProperty().bind(propertyMarkdownCheckboxLabel);
        searchBar.promptTextProperty().bind(propertySearchPlaceholder);
        renderedNoteTitleLabel.textProperty().bind(propertyRenderedNoteTitleDefaultLabel);

        setLocale(configLoader.getLocale());
    }

    /**
     * Getter for the list of saved notes.
     *
     * @return The list of saved notes.
     */
    public ObservableList<Note> getSavedNotes() {
        return savedNotes;
    }

    /**
     * Setter for the list of saved notes.
     *
     * @param newNotes The new list of saved notes.
     */
    public void setSavedNotes(ObservableList<Note> newNotes) {
        this.savedNotes = newNotes;
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

        Locale currentLocale = configLoader.getLocale();

        TextInputDialog dialog = new TextInputDialog();
        dialog.titleProperty().bind(propertyAddDialogTitle);
        dialog.contentTextProperty().bind(propertyAddDialogText);
        dialog.setHeaderText(null);

        Optional<String> result = dialog.showAndWait();
        result.ifPresent(title -> {
            if (title.trim().isEmpty()) {

                // Handling the invalid inputs
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.titleProperty().bind(propertyInvalidTitleAlertTitle);
                alert.contentTextProperty().bind(propertyInvalidTitleAlertText);
                alert.setHeaderText(null);
                alert.showAndWait();
            } else {
                Note newNote = new Note(title.trim(), "");
                savedNotes.add(newNote);
                noteServerUtils.storeNote(newNote);
                notesList.getSelectionModel().select(savedNotes.size() - 1);
                noteTitleInput.setText(newNote.getTitle());
                noteEditor.setText(newNote.getText());

                // Default message for empty note text
                renderedNoteView.getEngine().loadContent(
                        currentLocale.equals(Locale.ENGLISH) ?
                                "The note is empty" :
                                "De notitie is leeg");
                selectedNoteIndex = savedNotes.size() - 1;

                undoHandler.pushAction(new CreateNoteAction(
                        newNote,
                        newNote.getTitle(),
                        newNote.getText(),
                        selectedNoteIndex,
                        this));

                noteEditor.requestFocus();
            }
        });
    }

    /**
     * Deletes the currently selected note and stores this action in the
     * undo stack.
     * Parameterless overload used by the delete button.
     *
     * @return True if the deletion was successful, false if no note was selected
     * or the user cancelled the operation.
     */
    public boolean handleDeleteNote() {
        return handleDeleteNote(selectedNoteIndex, true);
    }

    /**
     * Deletes a note from the list of notes.
     *
     * @param index              The index of the note to delete in the list of notes.
     * @param isAddedToUndoStack True if the delete action is added to the undo handler's stack,
     *                           false if it is not added.
     * @return True if the deletion was successful, false if the index was -1
     * or the user cancelled the operation.
     */
    public boolean handleDeleteNote(int index, boolean isAddedToUndoStack) {
        Locale currentLocale = configLoader.getLocale();

        if (index == -1) {
            showWarning(currentLocale.equals(Locale.ENGLISH) ?
                            "No Note Selected" :
                            "Geen noot geselecteerd",
                    currentLocale.equals(Locale.ENGLISH) ?
                            "No note selected for deletion. Please select a note and try again." :
                            "Geen notitie geselecteerd voor verwijdering. " +
                                    "Selecteer een notitie en probeer het opnieuw.");
            return false;
        }

        Note deletedNote = savedNotes.get(index);

        if (showConfirmation(currentLocale.equals(Locale.ENGLISH) ?
                        "Delete Note" :
                        "Notitie verwijderen",
                (currentLocale.equals(Locale.ENGLISH) ?
                        "Are you sure you want to delete this note?\n" :
                        "Weet u zeker dat u deze notitie wilt verwijderen?\n") +
                        deletedNote.getTitle())) {

            noteServerUtils.deleteNote(deletedNote.getTitle());

            if (isAddedToUndoStack) {
                undoHandler.pushAction(new DeleteNoteAction(
                        deletedNote,
                        deletedNote.getTitle(),
                        deletedNote.getText(),
                        index,
                        this));
            }
            commitDeleteNote(index);
            return true;
        } else {
            updateDisplay();
            return false;
        }
    }

    /**
     * Undoes the deletion / redoes the creation of a note by
     * reinstating it in the list of notes and selecting it.
     *
     * @param deletedNote The entity corresponding to the deleted note.
     * @param noteIndex   The position the deleted noted had in the list of notes.
     */
    public void handleReAddNote(Note deletedNote, int noteIndex) {
        savedNotes.add(noteIndex, deletedNote);
        noteServerUtils.storeNote(deletedNote);
        notesList.getSelectionModel().select(noteIndex);
        selectedNoteIndex = noteIndex;
        noteTitleInput.setText(deletedNote.getTitle());
        noteEditor.setText(deletedNote.getText());

        renderedNoteTitleLabel.textProperty().unbind();
        renderedNoteTitleLabel.setText(deletedNote.getTitle());
        renderedNoteView.getEngine().loadContent(deletedNote.getText());
    }

    /**
     * A Warning pop-up message (only option is to say OK)
     *
     * @param title   The title of the pop-up message
     * @param message The body of the pop-up message
     */
    public void showWarning(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    /**
     * A Confirmation pop-up message (Options: OK / CANCEL)
     *
     * @param title   The title of the pop-up message
     * @param message The body of the pop-up message
     * @return True if the user pressed OK, false if the user pressed
     * CANCEL or closed the pop-up
     */
    public boolean showConfirmation(String title, String message) {
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
        Locale currentLocale = configLoader.getLocale();

        if (selectedNoteIndex != -1 && selectedNoteIndex < savedNotes.size()) {
            Note selectedNote = savedNotes.get(selectedNoteIndex);
            noteTitleInput.setText(selectedNote.getTitle());
            noteEditor.setText(selectedNote.getText());

            renderedNoteTitleLabel.textProperty().unbind();
            renderedNoteTitleLabel.setText(selectedNote.getTitle());
            renderedNoteView.getEngine().loadContent(selectedNote.getText());
        } else {
            selectedNoteIndex = -1;

            noteTitleInput.textProperty().removeListener(noteTitleInputListener);
            noteTitleInput.clear();
            noteTitleInput.textProperty().addListener(noteTitleInputListener);

            noteEditor.clear();
            renderedNoteTitleLabel.textProperty().unbind();
            renderedNoteTitleLabel.setText(currentLocale.equals(Locale.ENGLISH) ? "Untitled Note" :
                    "Naamloze notitie");
            renderedNoteView.getEngine().loadContent(currentLocale.equals(Locale.ENGLISH) ?
                    "The note is empty" :
                    "De notitie is leeg");
        }
    }


    /**
     * Adds a listener to the text editor to listen to changes made in the text,
     * so that they are automatically propagated in the rendered view.
     */
    public void updateDisplay() {

        Locale currentLocale = configLoader.getLocale();

        noteEditor.textProperty().addListener((_, _, newValue) -> {
            content = newValue;
            if (noteTitleInput.getText().isEmpty() && content.isEmpty()) {
                renderedNoteView.getEngine().loadContent(currentLocale.equals(Locale.ENGLISH) ?
                        "The note is empty" :
                        "De notitie is leeg");
            } else {
                renderedNoteView.getEngine().loadContent(markdownCtrl.markdownToHtml(content));
            }
        });

        if (selectedNoteIndex != -1) {
            // If a note is selected, update the content of that note
            Note selectedNote = savedNotes.get(selectedNoteIndex);

            //calling the noteServerUtils
            HashMap<String, String> patch = new HashMap<>();
            patch.put("title", selectedNote.getTitle());
            patch.put("text", content);
            noteServerUtils.updateNote(selectedNote.getTitle(), patch);

            selectedNote.setText(content);
            notesList.refresh();
        } else if (!content.isEmpty()) {
            renderedNoteTitleLabel.textProperty().unbind();
            renderedNoteTitleLabel.textProperty().bind(propertyRenderedNoteTitleLabelUntitled);
        }
    }

    /**
     * Checks the server to see if there are any notes
     * that have a different title to the ones stored locally
     * and adds them to the list.
     */
    public void handleRefreshNote() {
        List<String> titlesOfCurrentNotes = savedNotes
                .stream()
                .map(Note::getTitle)
                .toList();
        List<Note> serverNotes = noteServerUtils.getAllNotes();
        serverNotes.forEach(note -> {

            // This ignores the case in which the server has a different
            // version of a note with the same title.
            if (!titlesOfCurrentNotes.contains(note.getTitle())) {
                savedNotes.add(note);
            }
        });
        notesList.refresh();
    }

    /**
     * Displays the problems with Markdown rendering in the webview.
     */
    public void showProblems() {
        String renderedContent = markdownCtrl.markdownToHtml(content);
        if (problemsCheckbox.isSelected()) {
            noteEditor.setEditable(false);
            List<String> problems = markdownCtrl.getProblems(content);
            StringBuilder result = new StringBuilder();
            for (int i = 0; i < problems.size(); i++) {
                result.append(problems.get(i));
                if (i < problems.size() - 1)
                    result.append("\n");
            }
            renderedNoteView.getEngine().loadContent(result.toString());
        } else {
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
        Locale currentLocale = configLoader.getLocale();
        String updatedTitle = noteTitleInput.getText();

        renderedNoteTitleLabel.textProperty().unbind();
        if (updatedTitle.isEmpty()) {
            renderedNoteTitleLabel.textProperty().bind(propertyRenderedNoteTitleLabelUntitled);
        } else {
            renderedNoteTitleLabel.setText(updatedTitle);
        }

        Note selectedNote = notesList.getSelectionModel().getSelectedItem();

        if (selectedNote != null) {
            notesList.refresh();
            boolean isTitlePresent = noteServerUtils.getAllNotes().stream()
                    .anyMatch(note -> note.getTitle().equals(selectedNote.getTitle()));
            if (isTitlePresent) {
                HashMap<String, String> patch = new HashMap<>();
                patch.put("title", updatedTitle);
                noteServerUtils.updateNote(selectedNote.getTitle(), patch);
            }
            selectedNote.setTitle(updatedTitle);
        } else if (selectedNoteIndex == -1 &&
                (!savedNotes.isEmpty() || !noteEditor.getText().isEmpty())) {
            // Ensure the first note is created when only content exists
            String noteContent = noteEditor.getText();
            Note firstNote = new Note(updatedTitle, noteContent);
            savedNotes.add(firstNote);
            selectedNoteIndex = 0;
            notesList.getSelectionModel().select(0);
            notesList.refresh();
        }
    }


    /**
     * Allows the user to make their text bold.<br>
     * <br>
     * This method is triggered whenever the user presses Ctrl + B<br>
     * <br>
     * It checks if text is currently selected, if so then it wraps it with the Markdown syntax **.
     * If not, then it adds **|** where | represents user's cursor <br>
     */
    public void boldMarkdown() {
        String selectedText = noteEditor.getSelectedText();
        if (selectedText.isEmpty()) {
            int cursorPosition = noteEditor.getCaretPosition();
            String textBeforeCursor = noteEditor.getText(0, cursorPosition);
            String textAfterCursor = noteEditor.getText(cursorPosition, noteEditor.getLength());

            noteEditor.setText(textBeforeCursor + "**" + textAfterCursor + "**");
            noteEditor.positionCaret(cursorPosition + 2);

            noteEditor.requestFocus();
        } else {
            if (selectedText.startsWith("**") && selectedText.endsWith("**")) {
                selectedText = selectedText.substring(2, selectedText.length() - 2);
                noteEditor.replaceSelection(selectedText);
            } else {
                noteEditor.replaceSelection("**" + selectedText + "**");
            }
        }
    }

    /**
     * Allows the user to make their text italic.<br>
     * <br>
     * This method is triggered whenever the user presses Ctrl + I<br>
     * <br>
     * It checks if text is currently selected, if so then it wraps it with the Markdown syntax *.
     * If not, then it adds *|* where | represents user's cursor <br>
     */
    public void italicMarkdown() {
        String selectedText = noteEditor.getSelectedText();
        if (selectedText.isEmpty()) {
            int cursorPosition = noteEditor.getCaretPosition();
            String textBeforeCursor = noteEditor.getText(0, cursorPosition);
            String textAfterCursor = noteEditor.getText(cursorPosition, noteEditor.getLength());

            noteEditor.setText(textBeforeCursor + "*" + textAfterCursor + "*");
            noteEditor.positionCaret(cursorPosition + 1);

            noteEditor.requestFocus();
        } else {
            if (selectedText.startsWith("*") && selectedText.endsWith("*")) {
                selectedText = selectedText.substring(1, selectedText.length() - 1);
                noteEditor.replaceSelection(selectedText);
            } else {
                noteEditor.replaceSelection("*" + selectedText + "*");
            }
        }
    }

    /**
     * Allows the user to make their text underlined.<br>
     * <br>
     * This method is triggered whenever the user presses Ctrl + U<br>
     * <br>
     * It follows the same logic as bold and italic. <br>
     */
    public void underlineMarkdown() {
        String selectedText = noteEditor.getSelectedText();

        if (!selectedText.isEmpty()) {
            if (selectedText.startsWith("<u>") && selectedText.endsWith("</u>")) {
                selectedText = selectedText.substring(3, selectedText.length() - 4);
                noteEditor.replaceSelection(selectedText);
            } else {
                noteEditor.replaceSelection("<u>" + selectedText + "</u>");
            }
        } else {
            int cursorPos = noteEditor.getCaretPosition();
            String textBeforeCursor = noteEditor.getText(0, cursorPos);
            String textAfterCursor = noteEditor.getText(cursorPos, noteEditor.getLength());
            noteEditor.setText(textBeforeCursor + "<u>" + textAfterCursor + "</u>");


            noteEditor.positionCaret(cursorPos + 3);


            noteEditor.requestFocus();
        }
    }

    /**
     * Updates the application locale.
     *
     * @param l The new locale of the application.
     */
    public void setLocale(Locale l) {
        URL imageURL = getClass().getResource("/images/" + l.toString() + ".png");
        if (imageURL == null) {
            System.err.println("Could not load the image of the flag");
        } else {
            selectedLanguageImage.setImage(new Image(imageURL.toExternalForm()));
        }
        configLoader.setLocale(l);

        var rb = ResourceBundle.getBundle(Main.BUNDLE_NAME, l);

        String text = rb.getString("label.editorPlaceholder");
        propertyEditorPlaceholder.set(text);
        text = rb.getString("label.titlePlaceholder");
        propertyTitlePlaceholder.set(text);
        text = rb.getString("label.noteEditorName");
        propertyNoteEditorName.set(text);
        text = rb.getString("label.renderedViewName");
        propertyRenderedViewName.set(text);
        text = rb.getString("label.markdownCheckboxLabel");
        propertyMarkdownCheckboxLabel.set(text);
        text = rb.getString("label.searchPlaceholder");
        propertySearchPlaceholder.set(text);
        text = rb.getString("label.renderedNoteTitleDefaultLabel");
        propertyRenderedNoteTitleDefaultLabel.set(text);
        text = rb.getString("label.renderedNoteViewEmpty");
        propertyRenderedNoteViewEmpty.set(text);
        text = rb.getString("label.renderedNoteTitleLabelUntitled");
        propertyRenderedNoteTitleLabelUntitled.set(text);
        text = rb.getString("label.addDialogTitle");
        propertyAddDialogTitle.set(text);
        text = rb.getString("label.addDialogText");
        propertyAddDialogText.set(text);
        text = rb.getString("label.invalidTitleAlertTitle");
        propertyInvalidTitleAlertTitle.set(text);
        text = rb.getString("label.invalidTitleAlertText");
        propertyInvalidTitleAlertText.set(text);
        text = rb.getString("label.deleteNoteWarningTitle");
        propertyDeleteNoteWarningTitle.set(text);
        text = rb.getString("label.deleteNoteWarningText");
        propertyDeleteNoteWarningText.set(text);
        text = rb.getString("label.deleteNoteConfirmationTitle");
        propertyDeleteNoteConfirmationTitle.set(text);
        text = rb.getString("label.deleteNoteConfirmationText");
        propertyDeleteNoteConfirmationText.set(text);
    }

    /**
     * Sets the application locale to English.
     */
    public void setEN() {
        setLocale(Locale.ENGLISH);
    }

    /**
     * Sets the application locale to Dutch.
     */
    public void setNL() {
        setLocale(Locale.forLanguageTag("nl-NL"));
    }
}

