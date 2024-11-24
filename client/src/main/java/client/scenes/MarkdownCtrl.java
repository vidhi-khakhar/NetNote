package client.scenes;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TextArea;
import javafx.scene.web.WebView;
import org.commonmark.node.Node;
import org.commonmark.parser.Parser;
import org.commonmark.renderer.html.HtmlRenderer;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class MarkdownCtrl implements Initializable {

    @FXML
    private TextArea myTextArea;

    @FXML
    private WebView myWebView;

    /**
     * Initializes the WebView element.
     * It updates the WebView panel when the TextArea is updated.
     * @param url not used
     * @param resourceBundle not used
     */

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        myTextArea.setWrapText(true);
        myTextArea.textProperty().addListener((observable, oldValue, newValue) -> {
            myWebView.getEngine().loadContent(markdownToHtml());
        });
    }

    /**
     * Method that parses a markdown string to html.
     * @return html formatted string
     */

    public String markdownToHtml() {
        Parser parser = Parser.builder().build();
        Node document = parser.parse(myTextArea.getText());
        HtmlRenderer renderer = HtmlRenderer.builder().build();
        return renderer.render(document);
    }

    /**
     * Method that creates a temporary Html file
     * from the parsed markdown string.
     * Saves if you click anywhere in the app
     * (TextArea, Pane, WebView)
     * @throws IOException should the file not be found
     */

    public void saveHtml() throws IOException {
        File file = new File("markdown.html");
        FileWriter fileWriter = new FileWriter(file);
        fileWriter.write(markdownToHtml());
        fileWriter.close();
        file.deleteOnExit();
    }
}
