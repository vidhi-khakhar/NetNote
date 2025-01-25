package client.scenes;

import client.config.ConfigLoader;
import client.utils.ServerUtils;
import client.utils.UiUtils;
import client.utils.collection.CollectionServerStatus;
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

/**
 * Controller for the "Edit collections..." menu
 * Methods starting with "handle" are directly referenced in the FXML code
 */
public class EditCollectionsCtrl {

    @Inject
    private LangUtils langUtils;

    @Inject
    private CollectionServerUtils collectionServerUtils;

    @Inject
    private ServerUtils serverUtils;

    @Inject
    private ConfigLoader configLoader;

    @Inject
    private CollectionUtils collectionUtils;

    @Inject
    private MainCtrl mainCtrl;

    @Inject
    private UiUtils uiUtils;

    private final ObservableList<Collection> collections = FXCollections.observableArrayList();
    private CollectionAction currentAction = CollectionAction.NO_ACTION;
    private Map<String, StringProperty> props;
    private boolean isSelectedCollectionListenerDisabled = false;
    private String oldAlias;

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
    private Tooltip toolTipAddCollection;

    @FXML
    private Tooltip toolTipRemoveCollection;

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

    @FXML
    private Text serverStatusText;

    /**
     * Initializes the {@link EditCollectionsCtrl} controller by
     * binding the appropriate text properties and adding the
     * appropriate event listeners.
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
                    // Display alias and append "(Default)" if it's the default collection
                    String displayName = collection.getAlias();
                    if (configLoader.getDefaultCollectionUniqueName().
                            equals(collection.getCollectionUniqueName())) {
                        displayName += " (Default)";
                    }
                    setText(displayName);
                }
            }
        });

        collectionsList
                .getSelectionModel()
                .selectedItemProperty()
                .addListener((_, oldValue, newValue) -> {
                    if (isSelectedCollectionListenerDisabled) return;

                    if (currentAction == CollectionAction.NO_ACTION) {
                        updateServerStatusText(CollectionServerStatus.COLLECTION_EXISTS);
                    } else {
                        showActionConflictPopup();
                        isSelectedCollectionListenerDisabled = true; // Avoid infinite loop
                        collectionsList.getSelectionModel().select(oldValue);
                        isSelectedCollectionListenerDisabled = false;
                        return;
                    }
                    if (newValue == null) {
                        aliasInput.setText("");
                        serverInput.setText("");
                        uniqueNameInput.setText("");
                        return;
                    }
                    aliasInput.setText(newValue.getAlias());
                    serverInput.setText(newValue.getServer());
                    uniqueNameInput.setText(newValue.getCollectionUniqueName());
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
        toolTipAddCollection.textProperty().bind(props.get("tooltipAddCollection"));
        toolTipRemoveCollection.textProperty().bind(props.get("tooltipRemoveCollection"));
    }

    /**
     * Creates a new collection with the default title of "New collection" and
     * selects it. Does not send the data to the server until the user presses "Save".
     */
    public void handleAddCollection() {
        if (currentAction != CollectionAction.NO_ACTION) {
            showActionConflictPopup();
            return;
        }
        int newIndex = collectionUtils.createNewCollection(collections);
        collectionsList.getSelectionModel().selectIndices(newIndex);
        currentAction = CollectionAction.ADD_COLLECTION;
        serverInput.setText("http://localhost:8080/");
        updateServerStatusText(CollectionServerStatus.SERVER_NOT_REACHABLE);
    }

    /**
     * Removes the currently selected collection with a confirmation from the user.
     * Also commits the changes to the local list of collections.
     */
    public void handleRemoveCollection() {
        int currentIndex = collectionsList.getSelectionModel().getSelectedIndex();
        if (currentIndex == -1) {
            uiUtils.showPopUp(props.get("noCollectionSelectedAlertTitle").get(),
                    props.get("noCollectionSelectedAlertText").get(),
                    Alert.AlertType.ERROR);
            return;
        }
        Collection currentCollection = collections.get(currentIndex);
        if (currentCollection.getCollectionUniqueName().
                equals(configLoader.getDefaultCollectionUniqueName())) {
            uiUtils.showPopUp(props.get("CannotRemoveDefaultCollectionTitle").get(),
                    props.get("CannotRemoveDefaultCollectionText").get(),
                    Alert.AlertType.ERROR);
            return;
        }
        if (uiUtils.showPopUp(props.get("removeCollectionConfirmationTitle").get(),
                props.get("removeCollectionConfirmationText").get(),
                Alert.AlertType.CONFIRMATION)) {
            currentAction = CollectionAction.NO_ACTION;
            configLoader.removeCollection(collections.get(currentIndex).getCollectionUniqueName());
            collectionsList.getSelectionModel().selectIndices(
                    collectionUtils.removeCollection(collections, currentIndex, currentIndex)
            );
            mainCtrl.populateCollectionsDropdown();
        }
    }

    /**
     * Saves the local changes to the database.
     * Performs a different database call depending on the currently
     * initiated action. <br>
     * Called when pressing the "Save" button.
     */
    public void handleSave() {
        switch (currentAction) {
            case ADD_COLLECTION -> addCollection();
            case CHANGE_COLLECTION_ALIAS -> changeCollectionAlias();
        }
        updateServerStatusText();
        mainCtrl.populateCollectionsDropdown();
    }

    /**
     * Discards the current action and reverts the local state
     * to be in sync with the server. <br>
     * Called when pressing the "Discard" button.
     */
    public void handleDiscard() {
        switch (currentAction) {
            case ADD_COLLECTION -> {
                currentAction = CollectionAction.NO_ACTION;
                collectionsList.getSelectionModel().selectIndices(
                        collectionUtils.removeCollection(
                                collections,
                                collectionsList.getSelectionModel().getSelectedIndex(),
                                collections.size() - 1)
                );
            }
            case CHANGE_COLLECTION_ALIAS -> {
                currentAction = CollectionAction.NO_ACTION;
                aliasInput.setText(oldAlias);
            }
        }
    }

    /**
     * Creates a new collection if no collection is currently selected.
     * It otherwise initiates an alias change action for the currently selected collection. <br>
     * Called when the text in the alias input changes.
     */
    public void handleAliasChange() {
        if (collectionsList.getSelectionModel().getSelectedIndex() == -1) {
            uiUtils.showPopUp(props.get("cannotChangeAliasOfUnselectedCollectionAlertTitle").get(),
                    props.get("cannotChangeAliasOfUnselectedCollectionAlertText").get(),
                    Alert.AlertType.ERROR);
            aliasInput.setText("");
            return;
        }
        if (currentAction == CollectionAction.NO_ACTION) {
            currentAction = CollectionAction.CHANGE_COLLECTION_ALIAS;
            oldAlias = collections.get(collectionsList.getSelectionModel().getSelectedIndex())
                    .getAlias();
        }
    }

    /**
     * Disallows changes to the server address of an already added collection. <br>
     * Called when the text in the server input changes.
     */
    public void handleServerChange() {
        if (currentAction != CollectionAction.ADD_COLLECTION) {
            serverInput.undo();
            uiUtils.showPopUp(props.get("cannotModifyServerAlertTitle").get(),
                    props.get("cannotModifyServerAlertText").get(),
                    Alert.AlertType.ERROR);
        }
    }

    /**
     * Disallows changes to the unique name for an already added collection. <br>
     * Called when the text in the "Collection" input changes.
     */
    public void handleUniqueNameChange() {
        if (currentAction != CollectionAction.ADD_COLLECTION) {
            uniqueNameInput.undo();
            uiUtils.showPopUp(props.get("cannotModifyUniqueNameAlertTitle").get(),
                    props.get("cannotModifyUniqueNameAlertText").get(),
                    Alert.AlertType.ERROR);
        }
    }

    /**
     * Adds a new collection to the local list of collections in "config.json",
     * and creates it on the server if it doesn't already exist. <br>
     * Fails if the same collection was already added or if the server is not reachable.
     * Called when pressing "Save" if the current {@link CollectionAction} is
     * {@code ADD_COLLECTION}.
     */
    public void addCollection() {
        if (!serverUtils.isServerAvailable(serverInput.getText())) {
            uiUtils.showPopUp(props.get("serverNotReachableAlertTitle").get(),
                    props.get("serverNotReachableAlertText").get(),
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
            uiUtils.showPopUp(props.get("collectionAlreadyAddedAlertTitle").get(),
                    props.get("collectionAlreadyAddedAlertText").get(),
                    Alert.AlertType.ERROR);
            return;
        }
        currentAction = CollectionAction.NO_ACTION;
        if (getStatus(server, uniqueName) == CollectionServerStatus.COLLECTION_WILL_BE_CREATED) {
            collectionServerUtils.createCollection(newCollection, server);
            uiUtils.showPopUp(props.get("NewCollectionCreatedTitle").get(),
                    props.get("NewCollectionCreatedText").get(),
                    Alert.AlertType.INFORMATION);
        }
        configLoader.addCollection(newCollection);
        collections.set(collections.size() - 1, newCollection);
    }

    /**
     * Changes the alias of a collection in the configuration file and on the server. <br>
     * Returns the menu to its default state by setting the {@link CollectionAction} to
     * {@code NO_ACTION}.
     */
    public void changeCollectionAlias() {
        int currentIndex = collectionsList.getSelectionModel().getSelectedIndex();
        Collection currentCollection = collections.get(currentIndex);
        String newAlias = aliasInput.getText();

        currentCollection.setAlias(newAlias);
        collectionServerUtils.updateCollectionAlias(currentCollection.getCollectionUniqueName(),
                newAlias,
                currentCollection.getServer());
        List<Collection> configCollections = configLoader.getCollections();
        configCollections.get(currentIndex).setAlias(newAlias);
        configLoader.save();
        collectionsList.refresh();
        currentAction = CollectionAction.NO_ACTION;
    }

    /**
     * Get the current collection server status based on the server address and collection name.
     *
     * @param serverAddress The address of the server to ping
     * @param uniqueName    The name of the collection to search for on the server
     * @return The server status based on the provided address and collection name
     */
    public CollectionServerStatus getStatus(String serverAddress, String uniqueName) {
        if (!serverUtils.isServerAvailable(serverAddress))
            return CollectionServerStatus.SERVER_NOT_REACHABLE;
        List<Collection> serverCollections =
                collectionServerUtils.getAllCollections(serverAddress);
        return collectionUtils.collectionAlreadyExists(serverCollections, uniqueName) ?
                CollectionServerStatus.COLLECTION_EXISTS :
                CollectionServerStatus.COLLECTION_WILL_BE_CREATED;
    }

    /**
     * Updates the server status text in the UI based on the current server status
     * for the current collection.
     */
    public void updateServerStatusText() {
        String server = serverInput.getText();
        String uniqueName = uniqueNameInput.getText();
        updateServerStatusText(getStatus(server, uniqueName));
    }

    /**
     * Updates the server status text in the UI with the given value.
     *
     * @param status The status to display in the text.
     */
    public void updateServerStatusText(CollectionServerStatus status) {
        serverStatusText.textProperty().unbind();
        serverStatusText.textProperty().bind(props.get(status.getBundleLabel()));
    }

    /**
     * Alerts the user that they cannot perform an action because
     * another action is already in progress.
     */
    public void showActionConflictPopup() {
        uiUtils.showPopUp(props.get("actionConflictAlertTitle").get(),
                props.get("actionConflictAlertText").get(),
                Alert.AlertType.ERROR);
    }

    /**
     * handleMakeDefault is the onAction method for the "Make Default" Button.
     * It makes the current collection default and refreshes the view.
     */
    public void handleMakeDefault() {
        int currentIndex = collectionsList.getSelectionModel().getSelectedIndex();
        if (currentIndex == -1) {
            uiUtils.showPopUp(props.get("noCollectionSelectedAlertTitle").get(),
                    props.get("noCollectionToMakeDefaultText").get(),
                    Alert.AlertType.ERROR);
            return;
        }
        if (currentAction != CollectionAction.NO_ACTION) {
            showActionConflictPopup();
            return;
        }
        Collection currentCollection = collections.get(currentIndex);

        configLoader.setDefaultCollectionUniqueName(currentCollection.getCollectionUniqueName());
        configLoader.save();
        collectionsList.refresh();
        currentAction = CollectionAction.NO_ACTION;
    }
}
