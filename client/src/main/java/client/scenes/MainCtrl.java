package client.scenes;

import client.Main;
import client.UserConfigLoader;
import client.components.Notification;
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

    private final Map<String, StringProperty> props = new HashMap<>();
    private Scene mainView;
    private Stage stage;

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

    private final ChangeListener<String> webViewContentPropertyListener =
            (_, _, newValue) ->
                    renderedNoteView.getEngine().loadContent(newValue);

    /**
     * Initializes the controller.
     *
     * @param stage    Reference to the primary stage.
     * @param mainView Holds a reference to the mainView scene.
     */
    public void initialize(Stage stage, Scene mainView) {

        this.stage = stage;
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

        noteEditor.textProperty().addListener((_, _, newValue) -> {
            content = newValue;
            if (content.isEmpty()) {
                props.get("renderedNoteViewEmpty").addListener(webViewContentPropertyListener);
                renderedNoteView.getEngine().loadContent(props.get("renderedNoteViewEmpty").get());
            } else {
                props.get("renderedNoteViewEmpty").removeListener(webViewContentPropertyListener);
                renderedNoteView.getEngine().loadContent(markdownCtrl.markdownToHtml(content));
            }
        });

        initializeShortcuts();
        initializeTextProperties();

        updateDisplay();
    }

    private void initializeShortcuts() {
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
                        Notification.create(stage,  e.getMessage());
                    }
                }
                case "Ctrl+Y" -> {
                    try {
                        undoHandler.redoAction();
                    } catch (CannotRedoException e) {
                        Notification.create(stage,  e.getMessage());
                    }
                }
                case "Ctrl+N" -> handleAddNote();
                case "Ctrl+D" -> handleDeleteNote();
                case "Ctrl+B" -> boldMarkdown();
                case "Ctrl+I" -> italicMarkdown();
                case "Ctrl+U" -> underlineMarkdown();
            }
        });
    }

    private void initializeTextProperties() {
        var rb = ResourceBundle.getBundle(Main.BUNDLE_NAME, configLoader.getLocale());

        rb.keySet()
                .forEach(key -> {
                    String shortKey = key.split("\\.")[1];
                    StringProperty newProp = new SimpleStringProperty();
                    props.put(shortKey, newProp);
                });

        noteEditor.promptTextProperty().bind(props.get("editorPlaceholder"));
        noteTitleInput.promptTextProperty().bind(props.get("titlePlaceholder"));
        noteEditorLabel.textProperty().bind(props.get("noteEditorName"));
        renderedNoteLabel.textProperty().bind(props.get("renderedViewName"));
        problemsCheckbox.textProperty().bind(props.get("markdownCheckboxLabel"));
        searchBar.promptTextProperty().bind(props.get("searchPlaceholder"));
        renderedNoteTitleLabel.textProperty().bind(props.get("renderedNoteTitleDefaultLabel"));

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

        TextInputDialog dialog = new TextInputDialog();
        dialog.titleProperty().bind(props.get("addDialogTitle"));
        dialog.contentTextProperty().bind(props.get("addDialogText"));
        dialog.setHeaderText(null);

        Optional<String> result = dialog.showAndWait();
        result.ifPresent(title -> {
            if (title.trim().isEmpty()) {
                showPopUp(props.get("invalidTitleAlertTitle").get(),
                        props.get("invalidTitleAlertText").get(),
                        Alert.AlertType.ERROR);
            } else if(isTitlePresent(title)) {

                showPopUp(props.get("invalidTitleAlertTitle").get(),
                        props.get("invalidTitleAlertTitleExists").get(),
                        Alert.AlertType.ERROR);
            } else {
                Note newNote = new Note(title.trim(), "");
                savedNotes.add(newNote);
                noteServerUtils.storeNote(newNote);
                notesList.getSelectionModel().select(savedNotes.size() - 1);
                noteTitleInput.setText(newNote.getTitle());
                noteEditor.setText(newNote.getText());

                // Default message for empty note text
                props.get("renderedNoteViewEmpty").addListener(webViewContentPropertyListener);
                renderedNoteView.getEngine().loadContent(props.get("renderedNoteViewEmpty").get());
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
        if (index == -1) {
            showPopUp(props.get("deleteNoteWarningTitle").get(),
                    props.get("deleteNoteWarningText").get(),
                    Alert.AlertType.WARNING);
            return false;
        }

        Note deletedNote = savedNotes.get(index);

        if (showPopUp(props.get("deleteNoteConfirmationTitle").get(),
                props.get("deleteNoteConfirmationText").get() + '\n' + deletedNote.getTitle(),
                Alert.AlertType.CONFIRMATION)) {

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
        props.get("renderedNoteViewEmpty").removeListener(webViewContentPropertyListener);
        renderedNoteView.getEngine().loadContent(deletedNote.getText());
    }

    /**
     * A Warning pop-up message (only option is to say OK)
     *
     * @param title   The title of the pop-up message
     * @param message The body of the pop-up message
     * @param type    The type of the alert object
     * @return True if the user presses 'OK', false otherwise
     */
    public boolean showPopUp(String title, String message, Alert.AlertType type) {
        Alert alert = new Alert(type);
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
        if (selectedNoteIndex != -1 && selectedNoteIndex < savedNotes.size()) {
            Note selectedNote = savedNotes.get(selectedNoteIndex);
            noteTitleInput.setText(selectedNote.getTitle());
            noteEditor.setText(selectedNote.getText());

            renderedNoteTitleLabel.textProperty().unbind();
            renderedNoteTitleLabel.setText(selectedNote.getTitle());
            props.get("renderedNoteViewEmpty").removeListener(webViewContentPropertyListener);
            renderedNoteView
                    .getEngine()
                    .loadContent(markdownCtrl.markdownToHtml(selectedNote.getText()));
        } else {
            selectedNoteIndex = -1;

            noteTitleInput.textProperty().removeListener(noteTitleInputListener);
            noteTitleInput.clear();
            noteTitleInput.textProperty().addListener(noteTitleInputListener);

            noteEditor.clear();
            renderedNoteTitleLabel.textProperty().unbind();
            renderedNoteTitleLabel.textProperty().bind(props.get("renderedNoteTitleLabelUntitled"));

            props.get("renderedNoteViewEmpty").addListener(webViewContentPropertyListener);
            renderedNoteView.getEngine().loadContent(props.get("renderedNoteViewEmpty").get());
        }
    }


    /**
     * Adds a listener to the text editor to listen to changes made in the text,
     * so that they are automatically propagated in the rendered view.
     */
    public void updateDisplay() {
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
            renderedNoteTitleLabel.textProperty().bind(props.get("renderedNoteTitleLabelUntitled"));
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
     * Shows a dialog where the user can change the address of the server.
     * The user is notified whether the change was successful or unsuccessful.
     */
    public void handleConfigureIP() {
        TextInputDialog dialog = new TextInputDialog();
        String localAddressText = "\n" +
                props.get("addressConfigLocalAddress").get() +
                ": " +
                NoteServerUtils.getLocalAddress();
        dialog.titleProperty().bind(props.get("addressConfigDialogName"));
        dialog.setContentText(props.get("addressConfigCurrentAddress").get() +
                ": " +
                noteServerUtils.getServerAddress() +
                localAddressText);
        dialog.setHeaderText(null);
        Optional<String> result = dialog.showAndWait();
        if (result.isPresent()) {
            String newAddress = result.get();
            if (noteServerUtils.isServerAvailable(newAddress)) {
                noteServerUtils.setServerAddress(newAddress);
                showPopUp(props.get("addressConfigSuccessAlertTitle").get(),
                        props.get("addressConfigSuccessAlertText").get(),
                        Alert.AlertType.INFORMATION);
            } else {
                showPopUp(props.get("addressConfigAddressErrorTitle").get(),
                        props.get("addressConfigAddressErrorText").get(),
                        Alert.AlertType.ERROR);
            }
        }
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
            props.get("renderedNoteViewEmpty").removeListener(webViewContentPropertyListener);
            renderedNoteView.getEngine().loadContent(result.toString());
        } else {
            noteEditor.setEditable(true);
            props.get("renderedNoteViewEmpty").removeListener(webViewContentPropertyListener);
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
        Note selectedNote = notesList.getSelectionModel().getSelectedItem();

        renderedNoteTitleLabel.textProperty().unbind();
        if (updatedTitle.isEmpty()) {
            showPopUp(props.get("invalidTitleAlertTitle").get(),
                    props.get("invalidTitleAlertText").get(),
                    Alert.AlertType.ERROR);
            return;
        } else {
            renderedNoteTitleLabel.setText(updatedTitle);
        }

        if (selectedNote != null) {
            notesList.refresh();
            if (isTitlePresent(selectedNote.getTitle())) {
                if (!isTitlePresent(updatedTitle)) {
                    HashMap<String, String> patch = new HashMap<>();
                    patch.put("title", updatedTitle);
                    noteServerUtils.updateNote(selectedNote.getTitle(), patch);
                    selectedNote.setTitle(updatedTitle);
                } else if (!updatedTitle.equals(selectedNote.getTitle())) {
                    showPopUp(props.get("invalidTitleAlertTitle").get(),
                            props.get("invalidTitleAlertTitleExists").get(),
                            Alert.AlertType.ERROR);
                }
            }
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
     * Check for duplicate title
     * @param title title to check
     * @return whether the title already exists in the database
     */
    public boolean isTitlePresent(String title) {
        return noteServerUtils.
                getAllNotes().
                stream().
                anyMatch(note -> note.getTitle().equals(title));
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

        rb.keySet()
                .forEach(key -> {
                    String shortKey = key.split("\\.")[1];
                    props.get(shortKey).set(rb.getString(key));
                });
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

