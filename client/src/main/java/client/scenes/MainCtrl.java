package client.scenes;

import client.config.ConfigLoader;
import client.components.Notification;
import client.utils.NoteSearchUtils;
import client.utils.LangUtils;
import client.utils.UiUtils;
import client.utils.markdown.MarkdownShortcutUtils;
import client.utils.undo.*;
import client.utils.NoteServerUtils;
import client.utils.collection.CollectionServerUtils;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.inject.Inject;
import commons.Collection;
import commons.Note;
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
import javafx.scene.web.WebView;
import javafx.stage.Stage;
import javafx.util.Pair;

import java.net.URL;
import java.util.*;

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
    private String content = "";
    private ObservableList<Note> savedNotes = FXCollections.observableArrayList();
    private Map<String, StringProperty> props;

    private final Map<Note, Integer> indexMap = new HashMap<>();
    private Scene editCollectionsMenu;
    private Scene mainView;
    private Stage stage;
    private Label noNotesMatchYourQueryLabel;

    @Inject
    private MarkdownCtrl markdownCtrl;

    @Inject
    private UndoHandler undoHandler;

    @Inject
    private NoteServerUtils noteServerUtils;

    private final ObjectMapper mapper = new ObjectMapper();

    private final CollectionServerUtils collectionServerUtils = new CollectionServerUtils(mapper);

    @Inject
    private ConfigLoader configLoader;

    @Inject
    private LangUtils langUtils;

    @Inject
    private UiUtils uiUtils;

    @Inject
    private MarkdownShortcutUtils markdownShortcutUtils;

    @FXML
    private TextField searchBar;

    @FXML
    private ImageView selectedLanguageImage;

    @FXML
    private Label collectionLabel;

    @FXML
    private Label filterByTagLabel;

    @FXML
    private ListView<Note> notesList;

    @FXML
    private Label noteEditorLabel;

    @FXML
    private TextArea noteEditor;

    @FXML
    private Label renderedNoteTitleLabel;

    @FXML
    private CheckBox problemsCheckbox;

    @FXML
    private TextField noteTitleInput;

    private final ChangeListener<String> noteTitleInputListener =
            (_, _, _) -> updateTitle();

    @FXML
    private WebView renderedNoteView;

    @FXML
    private MenuItem editCollectionsDropdownOption;

    @FXML
    private MenuButton collectionDropdown;

    @FXML
    private Label renderedNoteLabel;

    private final ChangeListener<String> webViewContentPropertyListener =
            (_, _, newValue) ->
                    renderedNoteView.getEngine().loadContent(newValue);

    /**
     * Initializes the controller.
     *
     * @param stage               Reference to the primary stage.
     * @param mainView            Holds a reference to the MainView scene.
     * @param editCollectionsMenu Holds a reference to the EditCollections menu scene.
     */
    public void initialize(Stage stage, Scene mainView, Scene editCollectionsMenu) {

        this.stage = stage;
        this.mainView = mainView;
        this.editCollectionsMenu = editCollectionsMenu;

        props = langUtils.getProps();
        savedNotes = FXCollections.observableArrayList(noteServerUtils.getAllNotes());
        notesList.setItems(savedNotes);

        noNotesMatchYourQueryLabel = new Label();

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
                    if (indexMap.containsKey(selectedNote)) {
                        selectedNoteIndex = indexMap.get(selectedNote);
                    } else {
                        selectedNoteIndex = savedNotes.indexOf(selectedNote);
                    }

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

        searchBar.textProperty().addListener((_, _, newValue) ->
                handleSearch(newValue)
        );

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
                        Notification.create(stage, e.getMessage());
                    }
                }
                case "Ctrl+Y" -> {
                    try {
                        undoHandler.redoAction();
                    } catch (CannotRedoException e) {
                        Notification.create(stage, e.getMessage());
                    }
                }
                case "Ctrl+N" -> handleAddNote();
                case "Ctrl+D" -> handleDeleteNote();
                case "Ctrl+B" -> applyMarkdownShortcut("**", "**");
                case "Ctrl+I" -> applyMarkdownShortcut("*", "*");
                case "Ctrl+U" -> applyMarkdownShortcut("<u>", "</u>");
            }
        });
    }

    private void initializeTextProperties() {
        noteEditor.promptTextProperty().bind(props.get("editorPlaceholder"));
        noteTitleInput.promptTextProperty().bind(props.get("titlePlaceholder"));
        noteEditorLabel.textProperty().bind(props.get("noteEditorName"));
        renderedNoteLabel.textProperty().bind(props.get("renderedViewName"));
        problemsCheckbox.textProperty().bind(props.get("markdownCheckboxLabel"));
        searchBar.promptTextProperty().bind(props.get("searchPlaceholder"));
        renderedNoteTitleLabel.textProperty().bind(props.get("renderedNoteTitleDefaultLabel"));
        collectionDropdown.textProperty().bind(props.get("currentCollectionName"));
        collectionLabel.textProperty().bind(props.get("collectionLabel"));
        filterByTagLabel.textProperty().bind(props.get("filterByTagName"));
        editCollectionsDropdownOption.textProperty().bind(
                props.get("collectionsDropdownEditCollections"));
        noNotesMatchYourQueryLabel.textProperty().bind(props.get("noNotesMatchYourQuery"));

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
     * Opens a dialog to add a new note.
     * This method shows a small input dialog asking the user to enter a note title.
     * If the title is valid (not empty), it adds the title to the list of notes.
     * If the title is empty, an error message is displayed.
     */
    public void handleAddNote() {

        if (!getCurrentQuery().isEmpty()) {
            searchBar.clear();
            handleSearch("");
        }

        Optional<String> result = uiUtils.askUserForInput(
                props.get("addDialogTitle").get(),
                props.get("addDialogText").get(),
                null
        );
        result.ifPresent(title -> {
            if (title.trim().isEmpty()) {
                uiUtils.showPopUp(props.get("invalidTitleAlertTitle").get(),
                        props.get("invalidTitleAlertText").get(),
                        Alert.AlertType.ERROR);
            } else if (isTitlePresent(title)) {

                uiUtils.showPopUp(props.get("invalidTitleAlertTitle").get(),
                        props.get("invalidTitleAlertTitleExists").get(),
                        Alert.AlertType.ERROR);
            } else {
                Note newNote = new Note(title.trim(), "");
                savedNotes.add(newNote);
                noteServerUtils.storeNote(newNote);
                indexMap.put(newNote, savedNotes.size() - 1);
                notesList.setItems(savedNotes);
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

                if (!getCurrentQuery().isEmpty()) {
                    handleSearch(getCurrentQuery());
                }
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

        String previousQuery = getCurrentQuery();
        if (!getCurrentQuery().isEmpty()) {
            handleSearch("");
        }
        if (index == -1) {
            uiUtils.showPopUp(props.get("deleteNoteWarningTitle").get(),
                    props.get("deleteNoteWarningText").get(),
                    Alert.AlertType.WARNING);
            return false;
        }

        Note deletedNote = savedNotes.get(index);

        if (uiUtils.showPopUp(props.get("deleteNoteConfirmationTitle").get(),
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
            handleSearch(previousQuery);
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
            if (selectedNote.getText().isEmpty()) {
                props.get("renderedNoteViewEmpty").addListener(webViewContentPropertyListener);
                renderedNoteView.getEngine().loadContent(props.get("renderedNoteViewEmpty").get());
            }
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
        String localAddressText = "\n" +
                props.get("addressConfigLocalAddress").get() +
                ": " +
                NoteServerUtils.getLocalAddress();
        Optional<String> result = uiUtils.askUserForInput(
                props.get("addressConfigDialogName").get(),
                props.get("addressConfigCurrentAddress").get() +
                        ": " +
                        noteServerUtils.getServerAddress() +
                        localAddressText,
                null
        );
        if (result.isPresent()) {
            String newAddress = result.get();
            if (noteServerUtils.isServerAvailable(newAddress)) {
                noteServerUtils.setServerAddress(newAddress);
                uiUtils.showPopUp(props.get("addressConfigSuccessAlertTitle").get(),
                        props.get("addressConfigSuccessAlertText").get(),
                        Alert.AlertType.INFORMATION);
            } else {
                uiUtils.showPopUp(props.get("addressConfigAddressErrorTitle").get(),
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
        String previousTitle;

        renderedNoteTitleLabel.textProperty().unbind();
        if (updatedTitle.isEmpty()) {
            uiUtils.showPopUp(props.get("invalidTitleAlertTitle").get(),
                    props.get("invalidTitleAlertText").get(),
                    Alert.AlertType.ERROR);
            return;
        } else {
            renderedNoteTitleLabel.setText(updatedTitle);
        }
        Note selectedNote = getCurrentNote();

        if (selectedNote != null) {
            previousTitle = selectedNote.getTitle();
            if (!updatedTitle.equals(previousTitle) && isTitlePresent(updatedTitle)) {
                uiUtils.showPopUp(props.get("invalidTitleAlertTitle").get(),
                        props.get("invalidTitleAlertTitleExists").get(),
                        Alert.AlertType.ERROR);
            } else {

                selectedNote.setTitle(updatedTitle);

                try {
                    noteServerUtils.updateNote(previousTitle, Map.of("title", updatedTitle));
                } catch (Exception e) {
                    selectedNote.setTitle(previousTitle);
                    return;
                }

                if (notesList.getItems().contains(selectedNote)) {
                    notesList.refresh();
                }
            }
        } else if (!savedNotes.isEmpty() || !noteEditor.getText().isEmpty()) {
            // Ensure the first note is created when only content exists
            String noteContent = noteEditor.getText();
            Note firstNote = new Note(updatedTitle, noteContent);
            savedNotes.add(firstNote);
            selectedNoteIndex = savedNotes.size() - 1;
            indexMap.put(firstNote, savedNotes.size() - 1);
            notesList.setItems(FXCollections.observableArrayList(savedNotes));
            notesList.getSelectionModel().select(selectedNoteIndex);
            notesList.refresh();
            noteServerUtils.storeNote(firstNote);

            renderedNoteTitleLabel.textProperty().unbind();
            renderedNoteTitleLabel.setText(updatedTitle);
            renderedNoteView.getEngine().loadContent(markdownCtrl.markdownToHtml(noteContent));
        }
    }

    /**
     * Check for duplicate title
     *
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
     * Calls the {@link MarkdownShortcutUtils} to toggle the formatting for the current
     * text selection using the provided prefix and suffix, which are in Markdown syntax.
     *
     * @param prefix The Markdown formatting before the selected text
     * @param suffix The Markdown formatting after the selected text
     */
    public void applyMarkdownShortcut(String prefix, String suffix) {
        String selectedText = noteEditor.getSelectedText();
        int pos = noteEditor.getCaretPosition();
        Pair<String, Integer> newText =
                markdownShortcutUtils.toggleShortcut(selectedText, pos, prefix, suffix);
        noteEditor.replaceSelection(newText.getKey());
        noteEditor.positionCaret(newText.getValue());
    }

    /**
     * Returns the currently opened note.
     *
     * @return currently displayed note.
     */
    public Note getCurrentNote() {
        Note currentNote = null;
        if (selectedNoteIndex >= 0 && selectedNoteIndex < savedNotes.size()) {
            currentNote = savedNotes.get(selectedNoteIndex);
        }
        return currentNote;
    }

    /**
     * This method filters the notesList based on a given search query,
     * and it refreshes the notesList to display only the matching notes.
     *
     * @param query the text entered by the user in the search bar
     */
    public void handleSearch(String query) {
        Note currentNote = getCurrentNote();

        if (query.trim().isEmpty()) {
            notesList.setItems(FXCollections.observableArrayList(savedNotes));
            indexMap.clear();
            for (int i = 0; i < savedNotes.size(); i++) {
                indexMap.put(savedNotes.get(i), i);
            }

            if (currentNote != null) {
                selectedNoteIndex = savedNotes.indexOf(currentNote);
                notesList.getSelectionModel().select(selectedNoteIndex);
            }
            return;
        }

        List<Note> searchedNotes = NoteSearchUtils.search(savedNotes, query);

        indexMap.clear();
        for (Note note : searchedNotes) {
            indexMap.put(note, savedNotes.indexOf(note));
        }

        ObservableList<Note> filteredNotes = FXCollections.observableArrayList(searchedNotes);
        notesList.setItems(filteredNotes);

        if (currentNote != null && !filteredNotes.contains(currentNote)) {
            selectedNoteIndex = savedNotes.indexOf(currentNote);
        }
        if (!filteredNotes.isEmpty() && currentNote == null) {
            notesList.getSelectionModel().clearSelection();
        }
        if (filteredNotes.isEmpty()) {
            notesList.setPlaceholder(noNotesMatchYourQueryLabel);
        }
    }

    /**
     * Returns the currently searched query
     *
     * @return current text in search bar
     */
    public String getCurrentQuery() {
        return searchBar.getText().trim();
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
        langUtils.setLocale(l);
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

    /**
     * Sets the application locale to Romanian.
     */
    public void setRO() {
        setLocale(Locale.forLanguageTag("ro-RO"));
    }

    /**
     * Opens the "Edit Collections" scene to manage the current collections.
     */
    public void showEditCollectionsMenu() {
        Stage secondaryStage = new Stage();
        secondaryStage.titleProperty().bind(props.get("collectionsDropdownEditCollections"));
        secondaryStage.setScene(editCollectionsMenu);
        secondaryStage.show();
    }

    /**
     * Reloads all the elements inside the collections dropdown menu.
     */
    public void populateCollectionsDropdown() {
        collectionDropdown.getItems().clear();
        // Fetch collections (assuming CollectionServerUtils.getAll() returns a List<String>)
        List<Collection> collections = configLoader.getCollections();
        // Create and add MenuItems for each collection
        for (Collection collection : collections) {
            MenuItem itemCollection = new MenuItem(collection.getAlias());
            itemCollection.setOnAction(event -> {
                collectionDropdown.textProperty().unbind();
                collectionDropdown.setText(collection.getAlias());
                // Example: process or display the notes
                processNotes(collectionServerUtils
                        .getAllNotesInCollection(
                                collection.getCollectionUniqueName(),
                                collection.getServer()));
            });
            collectionDropdown.getItems().add(itemCollection);
        }
        collectionDropdown.getItems().add(editCollectionsDropdownOption);
    }

    /**
     * Updates the list of notes to show only the notes in the current collection.
     * @param notesInCollection The list of notes in the currently selected collection.
     */
    public void processNotes(List<Note> notesInCollection) {
        Note currentNote = getCurrentNote();

        List<Note> matchedRetrievedNotesWithActualNotes = new ArrayList<>();

        for(Note noteSaved : savedNotes) {
            for(Note noteRetrieved : notesInCollection) {
                if(noteSaved.getTitle().equals(noteRetrieved.getTitle())) {
                    matchedRetrievedNotesWithActualNotes.add(noteSaved);
                }
            }
        }

        indexMap.clear();
        for (Note note : matchedRetrievedNotesWithActualNotes) {
            indexMap.put(note, savedNotes.indexOf(note));
        }

        ObservableList<Note> filteredNotes = FXCollections.
                observableArrayList(matchedRetrievedNotesWithActualNotes);
        notesList.setItems(filteredNotes);

        if (currentNote != null && !filteredNotes.contains(currentNote)) {
            selectedNoteIndex = savedNotes.indexOf(currentNote);
        }
        if (!filteredNotes.isEmpty() && currentNote == null) {
            notesList.getSelectionModel().clearSelection();
        }
    }

}
