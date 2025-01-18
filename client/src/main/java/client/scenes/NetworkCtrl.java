package client.scenes;

import client.config.ConfigLoader;
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
    private String currentAddress = "";
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

    /**
     * Initializes the {@link NetworkCtrl} controller by
     * binding the appropriate text properties and adding the
     * appropriate event listeners.
     */
    public void initialize() {
        String localAddress = NoteServerUtils.getLocalAddress();
        currentAddress = null;

        ToggleGroup radioGroup = new ToggleGroup();
        radioNotes.setToggleGroup(radioGroup);
        radioCollections.setToggleGroup(radioGroup);
        radioFiles.setToggleGroup(radioGroup);

        localAddressText.setText(localAddress);

        serverList.setItems(servers);
        resultList.setItems(results);

        // Refresh list of available servers every 5 seconds
        new Timer().scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                Platform.runLater(() -> refreshServerList());
            }
        }, 0, 5000);

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
    }

    /**
     * Add all the servers with collections that the current client has access to
     * into the list of servers.
     */
    public void refreshServerList() {
        servers.clear();
        servers.addAll(configLoader.getCollections()
                .stream()
                .map(Collection::getServer)
                .distinct()
                .toList());
        // Ensure there is at least one address listed
        if (!servers.contains(NoteServerUtils.getLocalAddress()))
            servers.add(NoteServerUtils.getLocalAddress());
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
        content.putString(NoteServerUtils.getLocalAddress());
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
        List<String> newResults;
        if (type == NetworkMenuSelectionType.COLLECTIONS) {
            List<Collection> collections = collectionServerUtils.getAllCollections(serverAddress);
            newResults = collections.stream().map(Collection::getCollectionUniqueName).toList();
        } else {
            noteServerUtils.setServerAddress(serverAddress);
            List<Note> notes = noteServerUtils.getAllNotes();
            if (type == NetworkMenuSelectionType.NOTES) {
                newResults = notes.stream().map(Note::getTitle).toList();
            } else {
                List<EmbeddedFile> files = new ArrayList<>();
                notes.forEach(n ->
                        files.addAll(
                                fileServerUtils.getFilesFromNote(serverAddress, n.getTitle())));
                newResults = files.stream().map(EmbeddedFile::getName).toList();
            }
        }
        return newResults;
    }

    /**
     * The results will be all the notes in the currently selected server
     */
    public void handleShowNotes() {
        currentType = NetworkMenuSelectionType.NOTES;
        updateResults(currentAddress, currentType);
    }

    /**
     * The results will be all the collections in the currently selected server
     */
    public void handleShowCollections() {
        currentType = NetworkMenuSelectionType.COLLECTIONS;
        updateResults(currentAddress, currentType);
    }

    /**
     * The results will be all the embedded files in the currently selected server
     */
    public void handleShowFiles() {
        currentType = NetworkMenuSelectionType.FILES;
        updateResults(currentAddress, currentType);
    }
}
