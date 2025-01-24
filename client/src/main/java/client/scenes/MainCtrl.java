package client.scenes;

import client.components.NotificationType;
import client.config.ConfigLoader;
import client.components.Notification;
import client.utils.file.EmbeddedFileServerUtils;
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
import commons.EmbeddedFile;
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
import javafx.scene.text.Text;
import javafx.scene.web.WebView;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.util.Pair;
import org.apache.commons.lang3.StringUtils;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Collectors;

public class MainCtrl {

    private String content = "";
    private ObservableList<Note> savedNotes = FXCollections.observableArrayList();
    private Map<String, StringProperty> props;
    private int currentLanguageIndex;
    private boolean areMarkdownProblemsShown = false;

    private Scene editCollectionsMenu;
    private Scene mainView;
    private Scene networkMenu;
    private Scene shortcutsOverview;
    private Stage stage;
    private Label noNotesMatchYourQueryLabel;
    private Collection currentCollection;
    private Collection currentDefaultCollection;

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

    @Inject
    private EmbeddedFileServerUtils embeddedFileServerUtils;

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

    @FXML
    private Button networkButton;

    @FXML
    private Text selectedLanguageLabel;

    @FXML
    private MenuButton languageDropdown;

    /**
     * Initializes the controller.
     *
     * @param stage               Reference to the primary stage.
     * @param mainView            Holds a reference to the MainView scene.
     * @param editCollectionsMenu Holds a reference to the EditCollections menu scene.
     * @param networkMenu         Holds a reference to the NetworkMenu scene.
     * @param shortcutsOverview   Holds a reference to the shortcutsOverview scene.
     */
    public void initialize(Stage stage,
                           Scene mainView,
                           Scene editCollectionsMenu,
                           Scene networkMenu,
                           Scene shortcutsOverview) {
        if (configLoader.getCollections().size() == 1 &&
                configLoader.getCollections().getFirst().getAlias().equals("Main")) {
            Collection defaultMainCollection = new Collection(
                    configLoader.getDefaultCollectionUniqueName(),
                    configLoader.getCollections().getFirst().getAlias(),
                    configLoader.getCollections().getFirst().getServer());
            collectionServerUtils.createCollection(defaultMainCollection,
                    defaultMainCollection.getServer());
        }
        for (Collection collection : configLoader.getCollections()) {
            if (collection.getCollectionUniqueName().
                    equals(configLoader.getDefaultCollectionUniqueName())) {
                currentCollection = collection;
                currentDefaultCollection = collection;
            }
        }
        this.stage = stage;
        this.mainView = mainView;
        this.editCollectionsMenu = editCollectionsMenu;
        this.shortcutsOverview = shortcutsOverview;
        this.networkMenu = networkMenu;
        props = langUtils.getProps();
        savedNotes = FXCollections.observableArrayList(
                collectionServerUtils.getAllNotesInCollection(
                        currentCollection.getCollectionUniqueName(),
                        currentCollection.getServer()));
        processNotes(collectionServerUtils.getAllNotesInCollection(
                currentCollection.getCollectionUniqueName(),
                currentCollection.getServer()));
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
            handleMarkdownRendering();
        });
        searchBar.textProperty().addListener((_, _, newValue) ->
                handleSearch(newValue)
        );
        problemsCheckbox.selectedProperty().addListener((_, _, isSelected) -> {
            areMarkdownProblemsShown = isSelected;
            handleMarkdownRendering();
        });
        initializeShortcuts();
        initializeTextProperties();
        initializeCss();
        updateDisplay();
    }

    private void initializeShortcuts(){
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
                        Notification.create(stage, e.getMessage(), NotificationType.ERROR);
                    }
                }
                case "Ctrl+Y" -> {
                    try {
                        undoHandler.redoAction();
                    } catch (CannotRedoException e) {
                        Notification.create(stage, e.getMessage(), NotificationType.ERROR);
                    }
                }
                case "Ctrl+N" -> handleAddNote();
                case "Ctrl+D" -> handleDeleteNote();
                case "Ctrl+B" -> applyMarkdownShortcut("**", "**");
                case "Ctrl+I" -> applyMarkdownShortcut("*", "*");
                case "Ctrl+U" -> applyMarkdownShortcut("<u>", "</u>");
                case "Ctrl+L" -> cycleLanguage(false);
                case "Ctrl+Shift+L" -> cycleLanguage(true);
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
        collectionDropdown.setText(currentCollection.getAlias() + " (Default)");
        collectionLabel.textProperty().bind(props.get("collectionLabel"));
        filterByTagLabel.textProperty().bind(props.get("filterByTagName"));
        addedFilesLabel.textProperty().bind(props.get("addedFilesName"));
        editCollectionsDropdownOption.textProperty().bind(
                props.get("collectionsDropdownEditCollections"));
        noNotesMatchYourQueryLabel.textProperty().bind(props.get("noNotesMatchYourQuery"));
        networkButton.textProperty().bind(props.get("network"));

        setLocale(configLoader.getLocale());
    }


    /**
     * Loads a stylesheet ({@code .css}) file from the project root into the webview,
     * if there are any present. <br>
     * If there is more than one stylesheet, the first one encountered will be loaded.
     */
    public void initializeCss() {
        File root = new File("client/.."); // Trick to access the root of the project
        File[] files = root.listFiles();
        if (files == null) return;
        List<File> cssFiles = filterCssFiles(files);
        String cssLabel = getCssLabel(cssFiles);
        if (cssFiles.isEmpty()) {
            Notification.create(stage,
                    props.get(cssLabel).get(),
                    NotificationType.INFO);
            return;
        }
        String cssFileName = cssFiles.getFirst().getName();
        renderedNoteView.getEngine().setUserStyleSheetLocation(
                Paths.get(cssFileName).toUri().toString()
        );
        Notification.create(stage,
                props.get(cssLabel).get() + " " + cssFileName,
                NotificationType.INFO);
    }

    /**
     * Returns a list of all files ending in {@code .css} from an array of files
     * @param files The array of files to filter
     * @return A list of all CSS stylesheets in the array
     */
    public List<File> filterCssFiles(File[] files) {
        if (files == null) return new ArrayList<>();
        return Arrays
                .stream(files)
                .filter(f -> f.getName().endsWith(".css"))
                .toList();
    }

    /**
     * Returns the appropriate bundle label name given a list of CSS files
     * @param cssFiles The list of CSS files for which the length is tested
     * @return The appropriate bundle label name
     */
    public String getCssLabel(List<File> cssFiles) {
        if (cssFiles.size() > 1) {
            return "multipleCssFiles";
        } else if (cssFiles.size() == 1) {
            return "cssFound";
        }
        return "noCssFile";
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
        noteServerUtils.storeNote(note, currentCollection.getServer());

        collectionServerUtils.addNoteToCollection(
                currentDefaultCollection.getCollectionUniqueName(),
                note.getTitle(),
                currentDefaultCollection.getServer());

        if (currentCollection != currentDefaultCollection) {
            collectionServerUtils.addNoteToCollection(
                    currentCollection.getCollectionUniqueName(),
                    note.getTitle(),
                    currentCollection.getServer());
        }

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
        noteServerUtils.deleteNote(savedNotes.get(noteIndex).getTitle(),
                currentCollection.getServer());
        savedNotes.remove(noteIndex);
        filesHBox.getChildren().clear();
        if (!savedNotes.isEmpty()) {
            int newIndex = Math.min(noteIndex, savedNotes.size() - 1);
            notesList.getSelectionModel().select(newIndex);
        } else {
            notesList.getSelectionModel().clearSelection();
        }
    }

    /**
     * Moves a note from currently selected collection to the one chosen by the user.
     */
    public void handleMoveNote() {
        if (notesList.getSelectionModel().getSelectedItem() == null) {
            uiUtils.showPopUp(
                    props.get("noNoteSelectedTitle").get(),
                    props.get("noNoteSelectedMessage").get(),
                    Alert.AlertType.WARNING
            );
            return;
        }

        List<Collection> collections = configLoader.getCollections();
        Note selectedNote = notesList.getSelectionModel().getSelectedItem();
        Map<String, String> aliasToUniqueNameMap =
                getAliasToUniqueNameMap(configLoader.getCollections());
        List<String> otherCollectionsAliases = getOtherCollectionsAliases
                (configLoader.getCollections(), currentCollection.getAlias());

        if (otherCollectionsAliases.isEmpty()) {
            uiUtils.showPopUp(
                    props.get("noOtherCollectionsTitle").get(),
                    props.get("noOtherCollectionsMessage").get(),
                    Alert.AlertType.INFORMATION
            );
            return;
        }

        Optional<String> result = uiUtils.showChoiceDialog(
                props.get("moveNoteDialogTitle").get(),
                String.format(props.get("moveNoteDialogHeader").get(), selectedNote.getTitle()),
                props.get("moveNoteDialogContent").get(),
                otherCollectionsAliases,
                currentCollection.getAlias()
        );


        if (result.isEmpty()) {
            return;
        }
        String targetCollectionAlias = result.get();

        collectionServerUtils.moveNoteToAnotherCollection(
                currentCollection.getCollectionUniqueName(),
                selectedNote.getTitle(),
                aliasToUniqueNameMap.get(targetCollectionAlias),
                currentCollection.getServer()
        );

        processNotes(collectionServerUtils.getAllNotesInCollection(
                currentCollection.getCollectionUniqueName(),
                currentCollection.getServer()
        ));
    }

    /**
     * Creates a List of all collections Aliases,
     * without the one currently displayed.
     *
     * @param collections The list of current collections.
     * @param currentAlias The alias of currently displayed collection.
     * @return A list of Aliases of other collections.
     */
    public List<String> getOtherCollectionsAliases
    (List<Collection> collections, String currentAlias) {
        return collections.stream()
                .map(Collection::getAlias)
                .filter(alias -> !alias.equals(currentAlias))
                .toList();
    }

    /**
     * Generates a map that connects each collection alias with its unique name.
     *
     * @param collections The list of collections to process.
     * @return A map where the key is the collection alias, and the value is the unique name.
     */
    public Map<String, String> getAliasToUniqueNameMap
    (List<Collection> collections) {
        return collections.stream()
                .collect(Collectors.toMap
                        (Collection::getAlias, Collection::getCollectionUniqueName));
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
            filesHBox.getChildren().clear();
            content = note.getText();

            renderedNoteTitleLabel.textProperty().unbind();
            renderedNoteTitleLabel.setText(note.getTitle());
            if (note.getText().isEmpty() && !areMarkdownProblemsShown) {
                renderedNoteView.getEngine().loadContent(props.get("renderedNoteViewEmpty").get());
            }
            List<EmbeddedFile> files = embeddedFileServerUtils.getFilesFromNote(
                    currentCollection.getServer(), note.getTitle());
            if (files != null)
                for (int i = 0; i < files.size(); i++) {
                    addFileElement(files.get(i));
                }


            handleMarkdownRendering();
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
     * @param text    the text to search and highlight the keyword in
     * @param keyword the keyword to highlight in the text
     * @return the text with highlighted keyword occurrences
     */
    public String highlightKeywordInText(String text, String keyword) {
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
            noteServerUtils.updateNote(selectedNote.getTitle(),
                    patch,
                    currentCollection.getServer());

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
        List<Note> serverNotes = noteServerUtils.getAllNotes(currentCollection.getServer());
        noteUtils.sync(savedNotes, serverNotes);
        notesList.refresh();
    }

    /**
     * Displays the problems with Markdown rendering in the webview.
     */
    public void handleMarkdownRendering() {
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
                    noteServerUtils.updateNote(previousTitle,
                            Map.of("title", updatedTitle),
                            currentCollection.getServer());
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
            noteServerUtils.storeNote(firstNote, currentCollection.getServer());

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
                getAllNotes(currentCollection.getServer()).
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
            processNotes(collectionServerUtils
                    .getAllNotesInCollection(currentCollection.getCollectionUniqueName(),
                            currentCollection.getServer()));
            return;
        }

        List<Note> searchedNotes = NoteSearchUtils.search(collectionServerUtils
                        .getAllNotesInCollection(currentCollection.getCollectionUniqueName(),
                                currentCollection.getServer())
                , query);
        ObservableList<Note> filteredNotes = FXCollections.observableArrayList(searchedNotes);
        notesList.setItems(filteredNotes);

        if (!filteredNotes.isEmpty() && currentNote == null) {
            notesList.getSelectionModel().clearSelection();
        }

        if (filteredNotes.isEmpty()) {
            notesList.setPlaceholder(noNotesMatchYourQueryLabel);
        }
    }

    /**
     * A local test version of adding an embedded file
     */
    public void handleAddFile() throws IOException {
        if (notesList.getSelectionModel().getSelectedIndex() == -1) {
            uiUtils.showPopUp(props.get("FileAddWarningTitle").get(),
                    props.get("FileAddWarningText").get(),
                    Alert.AlertType.WARNING);
        } else {
            try {
                FileChooser fileChooser = new FileChooser();
                fileChooser.setTitle("Open File");
                File file = fileChooser.showOpenDialog(stage);
                if (file != null) {
                    EmbeddedFile embeddedFile = new EmbeddedFile(file.getName(),
                            Files.probeContentType(file.toPath()),
                            Files.readAllBytes(file.toPath()),
                            notesList.getSelectionModel().getSelectedItem());
                    if (!embeddedFileServerUtils.storeEmbeddedFile
                            (currentCollection.getServer(),
                                    notesList.getSelectionModel().getSelectedItem().getTitle(),
                                    embeddedFile)) {
                        uiUtils.showPopUp(props.get("fileStoreFailAlertTitle").get(),
                                props.get("fileStoreFailAlertText").get(),
                                Alert.AlertType.ERROR);
                        return;
                    }

                    addFileElement(embeddedFile);
                    String toAdd = constructLink(embeddedFile);
                    int caretPos = noteEditor.getCaretPosition();
                    noteEditor.insertText(caretPos, toAdd);
                    updateDisplay();
                } else
                    System.out.println("No file detected");

            } catch (OutOfMemoryError e) {
                uiUtils.showPopUp(props.get("fileTooLargeAlertTitle").get(),
                        props.get("fileTooLargeAlertText").get(), Alert.AlertType.ERROR);
            }
        }

    }


    /**
     * Renaming a file Pop-up
     */
    private void handleRenameFile(Label fileNameLabel, EmbeddedFile embeddedFile) {
        TextInputDialog dialog = new TextInputDialog(fileNameLabel.getText());
        dialog.titleProperty().bind(props.get("RenameFileConfirmationTitle"));
        dialog.headerTextProperty().bind(props.get("renameFileAlertText"));
        String type = embeddedFile.getType();
        type = type.substring(type.indexOf("/") + 1);
        // label.RenameFileConfirmationTitle=Rename File
        // label.renameFileAlertText= Enter a new name for the file:
        Optional<String> result = dialog.showAndWait();
        if (result.isPresent()) {
            String newTitle = result.get() + "." + type;
            fileNameLabel.setText(newTitle);
            embeddedFileServerUtils.renameEmbeddedFile(currentCollection.getServer(),
                    notesList.getSelectionModel().getSelectedItem().getTitle(),
                    embeddedFile.getName(), result.get());
            String oldLink = constructLink(embeddedFile);
            embeddedFile.setName(newTitle);
            String newLink = constructLink(embeddedFile);
            String editorResult = StringUtils.replace(noteEditor.getText(), oldLink, newLink);
            noteEditor.setText(editorResult);
            updateDisplay();
        }
    }


    /**
     * Delete an embedded file (currently only removes from HBox)
     */
    private void handleDeleteFile(HBox fileContainer, EmbeddedFile embeddedFile) {
        embeddedFileServerUtils.deleteEmbeddedFile(currentCollection.getServer(),
                notesList.getSelectionModel().getSelectedItem().getTitle(),
                embeddedFile.getName());
        String toDelete = constructLink(embeddedFile);
        String result = StringUtils.remove(noteEditor.getText(), toDelete);
        noteEditor.setText(result);
        filesHBox.getChildren().remove(fileContainer);
        updateDisplay();
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
        currentLanguageIndex = switch (l.toLanguageTag()) {
            case "nl-NL" -> 1;
            case "ro-RO" -> 2;
            case "pl-PL" -> 3;
            default -> 0; // English (case "en") is covered by the default case
        };
        selectedLanguageLabel.textProperty().bind(props.get(langUtils.getLabelFromLocale(l)));

        configLoader.setLocale(l);
        langUtils.setLocale(l);
        if (areMarkdownProblemsShown) renderMarkdownProblems();

        Notification.create(stage,
                props.get("languageSwitch").get(),
                NotificationType.INFO);
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
     * Sets the application locale to Polish.
     */
    public void setPL() {
        setLocale(Locale.forLanguageTag("pl-PL"));
    }

    /**
     * Selects the next language in the language list. <br>
     * If the current language is at the end of the list, it will loop back.
     *
     * @param reversed False if the selected language moves from top to bottom in the list
     *                 when cycling, true if it moves from bottom to top
     */
    public void cycleLanguage(boolean reversed) {
        int totalLanguages = languageDropdown.getItems().size();
        int newIndex = currentLanguageIndex + (reversed ? -1 : 1);
        if (newIndex >= totalLanguages) newIndex = 0;
        if (newIndex == -1) newIndex = totalLanguages - 1;
        languageDropdown.getItems().get(newIndex).fire();
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
     * Opens the "Network" scene to manage the data on different servers.
     */
    public void showNetworkMenu() {
        Stage secondaryStage = new Stage();
        secondaryStage.titleProperty().bind(props.get("network"));
        secondaryStage.setScene(networkMenu);
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

            final String displayName = configLoader.getDefaultCollectionUniqueName()
                    .equals(collection.getCollectionUniqueName())
                    ? collection.getAlias() + " (Default)"
                    : collection.getAlias();

            MenuItem itemCollection = new MenuItem(displayName);
            itemCollection.setOnAction(_ -> {
                currentCollection = collection;
                savedNotes.clear();
                savedNotes.addAll(collectionServerUtils.getAllNotesInCollection(
                        collection.getCollectionUniqueName(),
                        collection.getServer()));
                if (currentCollection.getCollectionUniqueName()
                        .equals(configLoader.getDefaultCollectionUniqueName())) {
                    currentDefaultCollection = collection;
                }
                collectionDropdown.textProperty().unbind();
                collectionDropdown.setText(displayName);
                // Example: process or display the notes
                processNotes(collectionServerUtils
                        .getAllNotesInCollection(
                                collection.getCollectionUniqueName(),
                                collection.getServer()));
            });
            collectionDropdown.getItems().add(itemCollection);
        }
        MenuItem itemAll = new MenuItem(props.get("AllCollectionDropdown").get());
        itemAll.setOnAction(_ -> {
            Set setOfNotes = new HashSet<>();
            for(Collection collection : configLoader.getCollections()) {
                for(Note note : collectionServerUtils.getAllNotesInCollection(
                        collection.getCollectionUniqueName(),collection.getServer())) {
                    setOfNotes.add(note);
                }
            }
            processNotes(setOfNotes.stream().toList());
        });
        collectionDropdown.getItems().add(itemAll);
        collectionDropdown.getItems().add(editCollectionsDropdownOption);
    }

    /**
     * Updates the list of notes to show only the notes in the current collection.
     *
     * @param notesInCollection The list of notes in the currently selected collection.
     */
    public void processNotes(List<Note> notesInCollection) {

        if (notesInCollection.isEmpty()) {
            notesList.setItems(FXCollections.observableArrayList());
            notesList.setPlaceholder(new Label(props.get("noNotesInCollection").get()));
            return;
        }

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

    /**
     * Helper method that adds a box for a file
     *
     * @param embeddedFile File that the HBox is based on
     */

    public void addFileElement(EmbeddedFile embeddedFile) {
        HBox fileContainer = new HBox(5);
        fileContainer.setStyle("-fx-border-color: black; -fx-background-color: white;" +
                " -fx-border-width: 1; -fx-padding: 5px; -fx-border-radius: 3;");

        Label fileNameLabel = new Label(embeddedFile.getName());

        Button renameButton = new Button("✎");
        renameButton.setOnAction(e -> handleRenameFile(fileNameLabel, embeddedFile));

        Button deleteButton = new Button("ⓧ");
        deleteButton.setOnAction(e -> handleDeleteFile(fileContainer, embeddedFile));

        fileContainer.getChildren().addAll(fileNameLabel, renameButton, deleteButton);

        filesHBox.getChildren().add(fileContainer);
    }

    /**
     *
     * @param embeddedFile file inserted from the user
     * @return path to the file on the server
     */

    public String constructLink(EmbeddedFile embeddedFile) {
        String toAdd = "";
        if (embeddedFile.getType().contains("image"))
            toAdd = "!";
        String fileName = URLEncoder.encode(embeddedFile.getName(),
                StandardCharsets.UTF_8).replaceAll("\\+", "%20");
        String noteName = URLEncoder.encode(notesList.getSelectionModel().
                        getSelectedItem().getTitle(),
                StandardCharsets.UTF_8).replaceAll("\\+", "%20");
        toAdd = toAdd + "[" + embeddedFile.getName() + "]("
                + currentCollection.getServer() + "api/files/" + noteName
                + "/content/" + fileName + ")";
        return toAdd;
    }

    /**
     * Opens the "Edit Collections" scene to manage the current collections
     */
    public void showShortcutsOverview() {
        Stage secondaryStage = new Stage();
        secondaryStage.setScene(shortcutsOverview);
        secondaryStage.show();
    }
}
