package client.scenes;

import client.config.ConfigLoader;
import client.utils.CollectionServerUtils;
import client.utils.LangUtils;
import com.google.inject.Inject;
import commons.Collection;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
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
}
