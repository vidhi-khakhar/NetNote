package client.scenes;

import client.config.ConfigLoader;
import client.utils.EmbeddedFileServerUtils;
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

    /**
     * Initializes the {@link NetworkCtrl} controller by
     * binding the appropriate text properties and adding the
     * appropriate event listeners.
     */
    public void initialize() {
        String localAddress = NoteServerUtils.getLocalAddress();
        currentAddress = localAddress;

        ToggleGroup radioGroup = new ToggleGroup();
        radioNotes.setToggleGroup(radioGroup);
        radioCollections.setToggleGroup(radioGroup);
        radioFiles.setToggleGroup(radioGroup);

        localAddressText.setText(localAddress);

        serverList.setItems(servers);
        resultList.setItems(results);

        servers.addAll(configLoader.getCollections()
                .stream()
                .map(Collection::getServer)
                .distinct()
                .toList());

        if (!servers.contains(localAddress))
            servers.add(localAddress);

        serverList.getSelectionModel().selectedItemProperty().addListener((_, _, selected) -> {
            currentAddress = selected;
            updateResults(currentAddress, currentType);
        });
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
        copyButton.setText("Copied!");
        Thread.ofPlatform().start(() -> {
            try {
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                System.out.println("Thread was interrupted");
            }
            // All UI changes must be performed by the main application thread
            Platform.runLater(() -> copyButton.setText("Copy"));
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
        results.clear();
        results.addAll(newResults);
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
