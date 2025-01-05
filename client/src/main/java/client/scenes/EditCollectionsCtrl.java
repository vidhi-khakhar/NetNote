package client.scenes;

import client.utils.LangUtils;
import com.google.inject.Inject;
import javafx.beans.property.StringProperty;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.text.Text;

import java.util.Map;

public class EditCollectionsCtrl {

    @Inject
    private LangUtils langUtils;

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

    public void initialize() {
        props = langUtils.getProps();

        menuTitle.textProperty().bind(props.get("editCollectionsMenuTitle"));
        labelTitle.textProperty().bind(props.get("editCollectionsMenuLabelTitle"));
        labelServer.textProperty().bind(props.get("editCollectionsMenuLabelServer"));
        labelCollection.textProperty().bind(props.get("editCollectionsMenuLabelCollection"));
        labelStatus.textProperty().bind(props.get("editCollectionsMenuLabelStatus"));
        buttonMakeDefault.textProperty().bind(props.get("buttonMakeDefault"));
        buttonSave.textProperty().bind(props.get("buttonSave"));
    }
}
