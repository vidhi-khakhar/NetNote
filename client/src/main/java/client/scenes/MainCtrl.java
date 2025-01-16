package client.scenes;

import client.config.ConfigLoader;
import client.components.Notification;
import client.utils.markdown.MarkdownUtils;
import client.utils.note.NoteSearchUtils;
import client.utils.note.NoteUtils;
import client.utils.LangUtils;
import client.utils.UiUtils;
import client.utils.markdown.MarkdownShortcutUtils;
import client.utils.undo.*;
import client.utils.note.NoteServerUtils;
import client.utils.collection.CollectionServerUtils;
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
import javafx.scene.layout.HBox;
import javafx.scene.web.WebView;
import javafx.stage.Stage;
import javafx.util.Pair;

import java.net.URL;
import java.util.*;

public class MainCtrl {

    private String content = "";
    private ObservableList<Note> savedNotes = FXCollections.observableArrayList();
    private Map<String, StringProperty> props;
    private boolean areMarkdownProblemsShown = false;
    private String currentQuery = ""; // Store the current search query


    private Scene editCollectionsMenu;
    private Scene mainView;
    private Stage stage;
    private Label noNotesMatchYourQueryLabel;

    @Inject
    private MarkdownUtils markdownUtils;

    @Inject
    private UndoHandler undoHandler;

    @Inject
    private NoteServerUtils noteServerUtils;

    @Inject
    private NoteUtils noteUtils;

    @Inject
    private CollectionServerUtils collectionServerUtils;

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
    private Label addedFilesLabel;

    @FXML
    private HBox filesHBox; // HBox in the FXML file

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
                    if (selectedNote != null) {
                        System.out.println("The selected note is on index: " +
                                notesList.getSelectionModel().getSelectedIndex());
                        noteTitleInput.setText(selectedNote.getTitle());
                        noteEditor.setText(selectedNote.getText());
                    }
                    displaySavedNote(selectedNote);
                });

        noteTitleInput.textProperty().addListener(noteTitleInputListener);

        noteEditor.textProperty().addListener((_, _, newValue) -> {
            content = newValue;
            handleMarkdownProblems();
        });

        searchBar.textProperty().addListener((_, _, newValue) ->
                handleSearch(newValue)
        );

        problemsCheckbox.selectedProperty().addListener((_, _, isSelected) -> {
            areMarkdownProblemsShown = isSelected;
            handleMarkdownProblems();
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
        addedFilesLabel.textProperty().bind(props.get("addedFilesName"));
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
                commitAddNote(newNote, savedNotes.size());

                // Default message for empty note text
                renderedNoteView.getEngine().loadContent(props.get("renderedNoteViewEmpty").get());

                undoHandler.pushAction(new CreateNoteAction(
                        newNote,
                        newNote.getTitle(),
                        newNote.getText(),
                        notesList.getSelectionModel().getSelectedIndex(),
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
        return handleDeleteNote(notesList.getSelectionModel().getSelectedIndex(), true);
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
     * Adds a new note to the list of notes without checks.
     *
     * @param note  The entity corresponding to the new note.
     * @param index The position of the new note in the list.
     */
    public void commitAddNote(Note note, int index) {
        savedNotes.add(index, note);
        noteServerUtils.storeNote(note);
        notesList.setItems(savedNotes);
        notesList.getSelectionModel().select(index);
        noteTitleInput.setText(note.getTitle());
        noteEditor.setText(note.getText());

        renderedNoteTitleLabel.textProperty().unbind();
        renderedNoteTitleLabel.setText(note.getTitle());
    }


    /**
     * Modifies the note list by deleting the note at a given index.
     *
     * @param noteIndex The index (in the note list) of the note to be deleted.
     */
    public void commitDeleteNote(int noteIndex) {
        noteServerUtils.deleteNote(savedNotes.get(noteIndex).getTitle());
        savedNotes.remove(noteIndex);
        if (!savedNotes.isEmpty()) {
            int newIndex = Math.min(noteIndex, savedNotes.size() - 1);
            notesList.getSelectionModel().select(newIndex);
        } else {
            notesList.getSelectionModel().clearSelection();
        }
    }

    /**
     * Updates the contents of the editor and the rendered view
     * after the user select a different note to load.
     *
     * @param note The note to display
     */
    public void displaySavedNote(Note note) {
        if (note != null) {
            noteTitleInput.setText(note.getTitle());
            noteEditor.setText(note.getText());
            content = note.getText();

            renderedNoteTitleLabel.textProperty().unbind();
            renderedNoteTitleLabel.setText(note.getTitle());

            handleMarkdownProblems();
        } else {
            noteTitleInput.textProperty().removeListener(noteTitleInputListener);
            noteTitleInput.clear();
            noteTitleInput.textProperty().addListener(noteTitleInputListener);

            noteEditor.clear();
            renderedNoteTitleLabel.textProperty().unbind();
            renderedNoteTitleLabel.textProperty().bind(props.get("renderedNoteTitleLabelUntitled"));

            renderedNoteView.getEngine().loadContent(props.get("renderedNoteViewEmpty").get());
        }
    }

    /**
     * Highlights all occurrences of the keyword in 'text' by wrapping them in a &lt;span&gt;
     * element with a yellow background color. The search is case-insensitive.
     *
     * @param text the text to search and highlight the keyword in
     * @param keyword the keyword to highlight in the text
     * @return the text with highlighted keyword occurrences
     */
    private String highlightKeywordInText(String text, String keyword) {
        if (keyword == null || keyword.isEmpty()) return text;
        // (?i) makes the search case-insensitive
        return text.replaceAll("(?i)(" +
                keyword + ")", "<span style=\"background-color: yellow;\">$1</span>");
    }

    /**
     * Adds a listener to the text editor to listen to changes made in the text,
     * so that they are automatically propagated in the rendered view.
     */
    public void updateDisplay() {
        Note selectedNote = notesList.getSelectionModel().getSelectedItem();

        if (selectedNote != null) {
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
        List<Note> serverNotes = noteServerUtils.getAllNotes();
        noteUtils.sync(savedNotes, serverNotes);
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
    public void handleMarkdownProblems() {
        String renderedContent = markdownUtils.markdownToHtml(content);
        boolean isChecked = problemsCheckbox.isSelected();

        if (isChecked) {
            noteEditor.setEditable(false);
            renderMarkdownProblems();
        } else {
            noteEditor.setEditable(true);
            if (content.isEmpty()) {
                renderedNoteView.getEngine().loadContent(props.get("renderedNoteViewEmpty").get());
            } else {
                // Check if a search query is active
                String query = getCurrentQuery();
                if (query != null && !query.isEmpty()) {
                    renderedContent = highlightKeywordInText(renderedContent, query);
                }
                renderedNoteView.getEngine().loadContent(renderedContent);
            }
        }
    }

    /**
     * Changes the contents of the WebView to show the Markdown problems
     */
    public void renderMarkdownProblems() {
        List<String> problems = markdownUtils.getProblems(content);
        StringBuilder result = new StringBuilder();
        if (problems.isEmpty()) {
            result.append(props.get("markdownNoProblems").get());
        } else {
            result.append("<ul>");
            for (String problem : problems) {
                result.append("<li>").append(problem).append("</li>");
            }
            result.append("</ul>");
        }
        renderedNoteView.getEngine().loadContent(result.toString());
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
        Note selectedNote = notesList.getSelectionModel().getSelectedItem();

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

            int newIndex = savedNotes.size() - 1;
            notesList.setItems(FXCollections.observableArrayList(savedNotes));
            notesList.getSelectionModel().select(newIndex);
            notesList.refresh();
            noteServerUtils.storeNote(firstNote);

            renderedNoteTitleLabel.textProperty().unbind();
            renderedNoteTitleLabel.setText(updatedTitle);
            renderedNoteView.getEngine().loadContent(markdownUtils.markdownToHtml(noteContent));
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
     * This method filters the notesList based on a given search query,
     * and it refreshes the notesList to display only the matching notes.
     *
     * @param query the text entered by the user in the search bar
     */
    public void handleSearch(String query) {
        Note currentNote = notesList.getSelectionModel().getSelectedItem();

        if (query.isBlank()) {
            notesList.setItems(FXCollections.observableArrayList(savedNotes));
            currentQuery = null; // Clear search query
            return;
        }

        List<Note> searchedNotes = NoteSearchUtils.search(savedNotes, query);
        ObservableList<Note> filteredNotes = FXCollections.observableArrayList(searchedNotes);
        notesList.setItems(filteredNotes);

        if (!filteredNotes.isEmpty() && currentNote == null) {
            notesList.getSelectionModel().clearSelection();
        }

        // Set the current search query for rendering
        currentQuery = query;

        if (filteredNotes.isEmpty()) {
            notesList.setPlaceholder(noNotesMatchYourQueryLabel);
        }
    }

    /**
     * A local test version of adding an embedded file
     */
    public void handleAddFile() {
        if (notesList.getSelectionModel().getSelectedIndex() == -1) {
            uiUtils.showPopUp(props.get("FileAddWarningTitle").get(),
                    props.get("FileAddWarningText").get(),
                    Alert.AlertType.WARNING);
        } else {

            HBox fileContainer = new HBox(5);
            fileContainer.setStyle("-fx-border-color: black; -fx-background-color: white;" +
                    " -fx-border-width: 1; -fx-padding: 5px; -fx-border-radius: 3;");

            Label fileNameLabel = new Label("testFile.jpg");

            Button renameButton = new Button("✎");
            renameButton.setOnAction(_ -> handleRenameFile(fileNameLabel));

            Button deleteButton = new Button("ⓧ");
            deleteButton.setOnAction(_ -> handleDeleteFile(fileContainer));

            fileContainer.getChildren().addAll(fileNameLabel, renameButton, deleteButton);

            filesHBox.getChildren().add(fileContainer);

        }

    }

    /**
     * Renaming a file Pop-up
     */
    private void handleRenameFile(Label fileNameLabel) {
        TextInputDialog dialog = new TextInputDialog(fileNameLabel.getText());
        dialog.titleProperty().bind(props.get("RenameFileConfirmationTitle"));
        dialog.headerTextProperty().bind(props.get("renameFileAlertText"));
        // label.RenameFileConfirmationTitle=Rename File
        // label.renameFileAlertText= Enter a new name for the file:
        Optional<String> result = dialog.showAndWait();
        result.ifPresent(fileNameLabel::setText);
    }

    /**
     * Delete an embedded file (currently only removes from HBox)
     */
    private void handleDeleteFile(HBox fileContainer) {
        filesHBox.getChildren().remove(fileContainer);
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
        if (areMarkdownProblemsShown) renderMarkdownProblems();
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
            itemCollection.setOnAction(_ -> {
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
     *
     * @param notesInCollection The list of notes in the currently selected collection.
     */
    public void processNotes(List<Note> notesInCollection) {
        Note currentNote = notesList.getSelectionModel().getSelectedItem();

        List<Note> matchedRetrievedNotesWithActualNotes = new ArrayList<>();

        for (Note noteSaved : savedNotes) {
            for (Note noteRetrieved : notesInCollection) {
                if (noteSaved.getTitle().equals(noteRetrieved.getTitle())) {
                    matchedRetrievedNotesWithActualNotes.add(noteSaved);
                }
            }
        }

        ObservableList<Note> filteredNotes = FXCollections.
                observableArrayList(matchedRetrievedNotesWithActualNotes);
        notesList.setItems(filteredNotes);

        if (!filteredNotes.isEmpty() && currentNote == null) {
            notesList.getSelectionModel().clearSelection();
        }
    }
}
