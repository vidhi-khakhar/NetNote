package client;

import static com.google.inject.Guice.createInjector;

import client.scenes.MainCtrl;
import client.scenes.MarkdownCtrl;
import com.google.inject.Injector;
import javafx.application.Application;
import javafx.stage.Stage;

public class TextFX extends Application {

    private static final Injector INJECTOR = createInjector(new MyModule());
    private static final MyFXML FXML = new MyFXML(INJECTOR);

    /**
     * Made a separate class file so that starting the JavaFX app
     * doesn't need the server started
     * @param args args string array
     */
    public static void main(String[] args) {
        launch(args);
    }

    /**
     * Starts the JavaFX app
     * @param stage empty stage
     * @throws Exception exceptions
     */

    @Override
    public void start(Stage stage) throws Exception {
        var markdown = FXML.load(MarkdownCtrl.class, "client", "scenes", "markdown.fxml");
        var mainCtrl = INJECTOR.getInstance(MainCtrl.class);
        stage.setTitle("Markdown");
        mainCtrl.initialize(stage, markdown);
    }
}
