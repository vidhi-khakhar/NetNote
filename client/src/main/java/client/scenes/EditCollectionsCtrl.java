package client.scenes;

import client.config.ConfigLoader;
import client.utils.CollectionServerUtils;
import client.utils.CollectionUtils;
import client.utils.LangUtils;
import com.google.inject.Inject;
import commons.Collection;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.text.Text;

import java.util.Map;

public class EditCollectionsCtrl {

    @Inject
    private LangUtils langUtils;

    @Inject
    private CollectionServerUtils collectionServerUtils;

    @Inject
    private ConfigLoader configLoader;

    @Inject
    private CollectionUtils collectionUtils;

    private final ObservableList<Collection> collections = FXCollections.observableArrayList();
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
    private ListView<Collection> collectionsList;

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
    }

    /**
     * Creates a new collection with the default title of "New collection" and
     * selects it. Does not send the data to the server until the user presses "Save".
     */
    public void handleAddCollection() {
        int newIndex = collectionUtils.createNewCollection(collections);
        collectionsList.getSelectionModel().selectIndices(newIndex);
    }
}
