package client.scenes;

import client.config.ConfigLoader;
import client.utils.ServerUtils;
import client.utils.file.EmbeddedFileServerUtils;
import client.utils.LangUtils;
import client.utils.collection.CollectionServerUtils;
import client.utils.network.NetworkMenuSelectionType;
import client.utils.note.NoteServerUtils;
import com.google.inject.Inject;
import commons.Collection;
import commons.EmbeddedFile;
import commons.Note;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.RadioButton;
import javafx.scene.control.ToggleGroup;
import javafx.scene.input.Clipboard;
import javafx.scene.input.ClipboardContent;
import javafx.scene.text.Text;

import java.util.*;

/**
 * Controller for the Network menu
 * Methods starting with "handle" are directly referenced in the FXML code
 */
public class NetworkCtrl {

    private final ObservableList<String> servers = FXCollections.observableArrayList();
    private final ObservableList<String> results = FXCollections.observableArrayList();
    private String currentAddress;
    private NetworkMenuSelectionType currentType = NetworkMenuSelectionType.NOTES;

    @Inject
    private ConfigLoader configLoader;

    @Inject
    private CollectionServerUtils collectionServerUtils;

    @Inject
    private NoteServerUtils noteServerUtils;

    @Inject
    private EmbeddedFileServerUtils fileServerUtils;

    @Inject
    private ServerUtils serverUtils;

    @Inject
    private LangUtils langUtils;

    @FXML
    private RadioButton radioNotes;

    @FXML
    private RadioButton radioCollections;

    @FXML
    private RadioButton radioFiles;

    @FXML
    private Button copyButton;

    @FXML
    private Text localAddressText;

    @FXML
    private ListView<String> serverList;

    @FXML
    private ListView<String> resultList;

    @FXML
    private Text menuTitle;

    @FXML
    private Text localAddressLabel;

    @FXML
    private Text serverListLabel;

    @FXML
    private Text resultListLabel;

    @FXML
    private Button refreshServerListButton;

    /**
     * Initializes the {@link NetworkCtrl} controller by
     * binding the appropriate text properties and adding the
     * appropriate event listeners.
     */
    public void initialize() {
        String localAddress = serverUtils.getLocalAddress();
        currentAddress = null;

        ToggleGroup radioGroup = new ToggleGroup();
        radioNotes.setToggleGroup(radioGroup);
        radioCollections.setToggleGroup(radioGroup);
        radioFiles.setToggleGroup(radioGroup);

        localAddressText.setText(localAddress);

        serverList.setItems(servers);
        resultList.setItems(results);

        refreshServerList();

        serverList.getSelectionModel().selectedItemProperty().addListener((_, _, selected) -> {
            currentAddress = selected;
            updateResults(currentAddress, currentType);
        });

        initializeTextProperties();
    }

    private void initializeTextProperties() {
        menuTitle.textProperty().bind(langUtils.getProps().get("network"));
        copyButton.textProperty().bind(langUtils.getProps().get("copy"));
        localAddressLabel.textProperty().bind(langUtils.getProps().get("localAddress"));
        radioNotes.textProperty().bind(langUtils.getProps().get("notes"));
        radioCollections.textProperty().bind(langUtils.getProps().get("collections"));
        radioFiles.textProperty().bind(langUtils.getProps().get("files"));
        serverListLabel.textProperty().bind(langUtils.getProps().get("serverList"));
        resultListLabel.textProperty().bind(langUtils.getProps().get("results"));
        refreshServerListButton.textProperty().bind(langUtils.getProps().get("refresh"));
    }

    /**
     * Add all the servers with collections that the current client has access to
     * into the list of servers.
     */
    public void refreshServerList() {
        servers.clear();
        List<Collection> collections = configLoader.getCollections();
        if (collections != null) {
            servers.addAll(configLoader.getCollections()
                    .stream()
                    .map(Collection::getServer)
                    .distinct()
                    .toList());
        }
        // Ensure there is at least one address listed
        if (!servers.contains(serverUtils.getLocalAddress()))
            servers.add(serverUtils.getLocalAddress());
    }

    /**
     * Copies the address of the local server in the current network
     * to the system clipboard. <br>
     * Changes the text of the button to "Copied!" after the copy operation,
     * then back to "Copy" after 2 seconds.
     */
    public void copyLocalAddressToClipboard() {
        Clipboard clipboard = Clipboard.getSystemClipboard();
        ClipboardContent content = new ClipboardContent();
        content.putString(serverUtils.getLocalAddress());
        clipboard.setContent(content);
        copyButton.textProperty().bind(langUtils.getProps().get("copied"));
        Thread.ofPlatform().start(() -> {
            try {
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                System.out.println("Thread was interrupted");
            }
            // All UI changes must be performed by the main application thread
            Platform.runLater(() -> copyButton
                    .textProperty()
                    .bind(langUtils.getProps().get("copy")));
        });
    }

    /**
     * Updates the list of results based on the current server and the type of data
     * to be shown.
     *
     * @param serverAddress The address of the server for which to display the data.
     * @param type          The type of data on the server to be displayed (notes, collections or
     *                      embedded files).
     */
    public void updateResults(String serverAddress, NetworkMenuSelectionType type) {
        if (currentAddress == null) {
            Text placeholderText = new Text();
            placeholderText.textProperty().bind(langUtils.getProps().get("networkMenuNoServer"));
            resultList.setPlaceholder(placeholderText);
            return;
        }
        results.clear();
        results.addAll(getNewResults(serverAddress,
                type,
                noteServerUtils,
                collectionServerUtils,
                fileServerUtils));
        if (results.isEmpty()) {
            Text placeholderText = new Text();
            placeholderText.textProperty().bind(langUtils.getProps().get("networkMenuNoResults"));
            resultList.setPlaceholder(placeholderText);
        }
    }

    /**
     * Returns a list of results based on the chosen category of data
     * (notes, collections or embedded files).
     *
     * @param serverAddress         The address of the server from which to fetch the data
     * @param type                  The chosen category of data
     * @param noteServerUtils       The class from which to fetch the notes
     * @param collectionServerUtils The class from which to fetch the collections
     * @param fileServerUtils       The class from which to fetch the embedded files
     * @return A list of results based on the chosen category of data.
     */
    public List<String> getNewResults(String serverAddress,
                                      NetworkMenuSelectionType type,
                                      NoteServerUtils noteServerUtils,
                                      CollectionServerUtils collectionServerUtils,
                                      EmbeddedFileServerUtils fileServerUtils) {
        return switch (type) {
            case COLLECTIONS -> getNewCollectionResults(serverAddress, collectionServerUtils);
            case NOTES -> getNewNoteResults(serverAddress, noteServerUtils);
            case FILES -> getNewFileResults(serverAddress, noteServerUtils, fileServerUtils);
        };
    }

    /**
     * Returns a list of the unique names of all collections stored on a server
     *
     * @param serverAddress         The address of the server
     * @param collectionServerUtils The class from which to fetch the collections
     * @return A list of the unique names of all collections stored on a server
     */
    public List<String> getNewCollectionResults(String serverAddress,
                                                CollectionServerUtils collectionServerUtils) {
        List<Collection> collections = collectionServerUtils.getAllCollections(serverAddress);
        return collections.stream().map(Collection::getCollectionUniqueName).toList();
    }

    /**
     * Returns a list of the titles of all notes stored on a server
     *
     * @param serverAddress   The address of the server
     * @param noteServerUtils The class from which to fetch the notes
     * @return A list of the titles of all notes stored on a server
     */
    public List<String> getNewNoteResults(String serverAddress,
                                          NoteServerUtils noteServerUtils) {
        List<Note> notes = noteServerUtils.getAllNotes(serverAddress);
        return notes.stream().map(Note::getTitle).toList();
    }

    /**
     * Returns a list of the names of all embedded files stored on a server
     *
     * @param serverAddress   The address of the server
     * @param noteServerUtils The class from which to fetch the notes
     * @param fileServerUtils The class from which to fetch the files
     * @return A list of the names of all embedded files stored on a server
     */
    public List<String> getNewFileResults(String serverAddress,
                                          NoteServerUtils noteServerUtils,
                                          EmbeddedFileServerUtils fileServerUtils) {
        List<Note> notes = noteServerUtils.getAllNotes(serverAddress);
        List<EmbeddedFile> files = new ArrayList<>();
        notes.forEach(n -> {
            List<EmbeddedFile> currentFiles =
                    fileServerUtils.getFilesFromNote(serverAddress, n.getTitle());
            if (currentFiles != null) {
                files.addAll(currentFiles);
            }
        });
        return files.stream().map(EmbeddedFile::getName).toList();
    }

    /**
     * The results will be all the notes in the currently selected server
     */
    public void handleShowNotesForSelectedServer() {
        currentType = NetworkMenuSelectionType.NOTES;
        updateResults(currentAddress, currentType);
    }

    /**
     * The results will be all the collections in the currently selected server
     */
    public void handleShowCollectionsForSelectedServer() {
        currentType = NetworkMenuSelectionType.COLLECTIONS;
        updateResults(currentAddress, currentType);
    }

    /**
     * The results will be all the embedded files in the currently selected server
     */
    public void handleShowFilesForSelectedServer() {
        currentType = NetworkMenuSelectionType.FILES;
        updateResults(currentAddress, currentType);
    }
}
