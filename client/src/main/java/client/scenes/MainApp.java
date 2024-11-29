package client.scenes;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class MainApp extends Application {
    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        Parent root = FXMLLoader.load(getClass().getResource("MainView.fxml"));
        Scene scene = new Scene(root);
        String css = getClass().getResource("gui.css").toExternalForm();
        scene.getStylesheets().add(css);

        primaryStage.setTitle("NetNote Application");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

}
