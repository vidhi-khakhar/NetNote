package client.scenes;

import client.config.ConfigLoader;
import client.utils.NoteServerUtils;
import client.utils.collection.CollectionServerUtils;
import client.utils.collection.CollectionUtils;
import client.utils.collection.CollectionAction;
import client.utils.LangUtils;
import com.google.inject.Inject;
import commons.Collection;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.text.Text;

import java.util.List;
import java.util.Map;

public class EditCollectionsCtrl {

    @Inject
    private LangUtils langUtils;

    @Inject
    private CollectionServerUtils collectionServerUtils;

    @Inject
    private NoteServerUtils noteServerUtils;

    @Inject
    private ConfigLoader configLoader;

    @Inject
    private CollectionUtils collectionUtils;

    @Inject
    private MainCtrl mainCtrl;

    private final ObservableList<Collection> collections = FXCollections.observableArrayList();
    private CollectionAction currentAction = CollectionAction.NO_ACTION;
    private Map<String, StringProperty> props;

    @FXML
    private Text menuTitle;

    @FXML
    private Label labelTitle;

    @FXML
    private Label labelServer;

    @FXML
    private Label labelCollection;

    @FXML
    private Label labelStatus;

    @FXML
    private Button buttonMakeDefault;

    @FXML
    private Button buttonSave;

    @FXML
    private Button buttonDiscard;

    @FXML
    private ListView<Collection> collectionsList;

    @FXML
    private TextField aliasInput;

    @FXML
    private TextField serverInput;

    @FXML
    private TextField uniqueNameInput;

    /**
     * Initializes the {@link EditCollectionsCtrl} controller by
     * setting the binding the appropriate text properties.
     */
    public void initialize() {
        props = langUtils.getProps();

        collections.addAll(configLoader.getCollections());
        collectionsList.setItems(collections);

        // The callback below is used to only display the name of the collection
        // in the list, instead of the whole output of `toString()`.
        // This implementation is borrowed from the one used in the MainCtrl class.
        collectionsList.setCellFactory(_ -> new ListCell<>() {
            @Override
            protected void updateItem(Collection collection, boolean empty) {
                super.updateItem(collection, empty);
                if (empty || collection == null) {
                    setText(null);
                } else {
                    setText(collection.getAlias());
                }
            }
        });

        initializeTextProperties();
    }

    private void initializeTextProperties() {
        menuTitle.textProperty().bind(props.get("editCollectionsMenuTitle"));
        labelTitle.textProperty().bind(props.get("editCollectionsMenuLabelTitle"));
        labelServer.textProperty().bind(props.get("editCollectionsMenuLabelServer"));
        labelCollection.textProperty().bind(props.get("editCollectionsMenuLabelCollection"));
        labelStatus.textProperty().bind(props.get("editCollectionsMenuLabelStatus"));
        buttonMakeDefault.textProperty().bind(props.get("buttonMakeDefault"));
        buttonSave.textProperty().bind(props.get("buttonSave"));
        buttonDiscard.textProperty().bind(props.get("buttonDiscard"));
    }

    /**
     * Creates a new collection with the default title of "New collection" and
     * selects it. Does not send the data to the server until the user presses "Save".
     */
    public void handleAddCollection() {
        if (currentAction != CollectionAction.NO_ACTION) {
            mainCtrl.showPopUp("Action already in progress",
                    "Please discard the current action to proceed.",
                    Alert.AlertType.ERROR);
            return;
        }
        currentAction = CollectionAction.ADD_COLLECTION;
        int newIndex = collectionUtils.createNewCollection(collections);
        collectionsList.getSelectionModel().selectIndices(newIndex);

    }

    public void handleSave() {
        switch (currentAction) {
            case ADD_COLLECTION -> storeCollection();
        }
    }

    public void handleDiscard() {
        switch (currentAction) {
            case ADD_COLLECTION -> {
                collectionUtils.removeLastCollection(
                        collections,
                        collectionsList.getSelectionModel().getSelectedIndex());
                currentAction = CollectionAction.NO_ACTION;
            }
        }
    }

    public void storeCollection() {
        if (!noteServerUtils.isServerAvailable(serverInput.getText())) {
            mainCtrl.showPopUp("Server not reachable",
                    "Server not reachable",
                    Alert.AlertType.ERROR);
            return;
        }
        String uniqueName = uniqueNameInput.getText();
        String server = serverInput.getText();
        String alias = aliasInput.getText();
        Collection newCollection = new Collection(
                uniqueName,
                alias,
                server
        );
        if (collectionUtils.collectionAlreadyExists(collections, uniqueName)) {
            mainCtrl.showPopUp("Note already added",
                    "Note already added",
                    Alert.AlertType.ERROR);
            return;
        }
        List<Collection> serverCollections =
                collectionServerUtils.getAllCollections(server);
        if (!collectionUtils.collectionAlreadyExists(serverCollections, uniqueName)) {
            collectionServerUtils.createCollection(newCollection, server);
        }
        configLoader.addCollection(newCollection);
        currentAction = CollectionAction.NO_ACTION;
    }
}
