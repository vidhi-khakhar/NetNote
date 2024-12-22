/*
 * Copyright 2021 Delft University of Technology
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package client;

import static com.google.inject.Guice.createInjector;

import client.scenes.MainCtrl;
import client.utils.NoteServerUtils;
import com.google.inject.Injector;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.util.Locale;

public class Main extends Application {

    private static final Injector INJECTOR = createInjector(new MyModule());
    private static final MyFXML FXML = new MyFXML(INJECTOR);
    public static final Locale DEFAULT_LOCALE = Locale.ENGLISH;
    public static final String BUNDLE_NAME = "labels";

    /**
     * Launches the client application
     *
     * @param args Command line arguments (not used)
     */
    public static void main(String[] args) {
        launch();
    }

    /**
     * Method that runs when the application is started
     *
     * @param primaryStage The stage component of the application
     */
    @Override
    public void start(Stage primaryStage) {

        var serverUtils = INJECTOR.getInstance(NoteServerUtils.class);
        if (!serverUtils.isServerAvailable()) {
            var msg = "Server needs to be started before the client, " +
                    "but it does not seem to be available. Shutting down.";
            System.err.println(msg);
            return;
        }

        var mainView = FXML.load(MainCtrl.class, "client", "scenes", "MainView.fxml");
        var mainViewScene = new Scene(mainView.getValue());
        primaryStage.setTitle("NetNote");
        primaryStage.setScene(mainViewScene);
        primaryStage.show();

        var mainCtrl = INJECTOR.getInstance(MainCtrl.class);
        mainCtrl.initialize(primaryStage, mainViewScene);
        mainCtrl.updateDisplay();
    }
}