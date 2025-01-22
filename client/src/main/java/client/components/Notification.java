package client.components;

import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.scene.Scene;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.util.Duration;

public class Notification {

    public static final int messageTime = 2000;
    public static final int fadeIn = 500;
    public static final int fadeOut = 500;
    private static NotificationType type;

    /**
     * Creates a pop-up notification that lasts 3 seconds.
     * @param main the Stage on which the notification will be displayed.
     * @param description the text of the notification
     * @param type The type of notification (error / info / warning etc.)
     */
    public static void create(Stage main, String description, NotificationType type)
    {
        Notification.type = type;
        Stage notificatonStage=new Stage();
        notificatonStage.initOwner(main);
        notificatonStage.setResizable(false);
        notificatonStage.initStyle(StageStyle.TRANSPARENT);

        Text text = new Text(description);
        text.setFont(Font.font("Bold", 12));
        text.setFill(Color.WHITESMOKE);

        Scene scene = createScene(text);
        notificatonStage.setScene(scene);

        setPosition(main, notificatonStage);

        notificatonStage.show();

        Timeline timelineIN = new Timeline();
        KeyFrame fadeInKey = new KeyFrame(Duration.millis(fadeIn),
                new KeyValue (notificatonStage.getScene().getRoot().opacityProperty(), 1));
        timelineIN.getKeyFrames().add(fadeInKey);
        timelineIN.setOnFinished((_) ->
            new Thread(() -> {
                try
                {
                    Thread.sleep(messageTime);
                }
                catch (InterruptedException e)
                {
                    System.err.println("Thread was interrupted");
                }
                Timeline timelineOut = new Timeline();
                KeyFrame fadeOutKey = new KeyFrame(Duration.millis(fadeOut),
                        new KeyValue (notificatonStage.getScene().getRoot().opacityProperty(), 0));
                timelineOut.getKeyFrames().add(fadeOutKey);
                timelineOut.setOnFinished((_) -> notificatonStage.close());
                timelineOut.play();
            }).start()
        );
        timelineIN.play();

        main.requestFocus(); // Ensures the main view does not lose focus
    }

    /**
     * Sets the position of the notification in the bottom right
     * of the main stage.
     * @param main the stage on which the notification will be displayed
     * @param notificationStage the stage of the notification
     */
    private static void setPosition(Stage main, Stage notificationStage) {
        notificationStage.setOnShown(_ -> {
            double screenWidth = main.getScene().getWindow().getWidth();
            double screenHeight = main.getScene().getWindow().getHeight();
            double notificationWidth = notificationStage.getWidth();
            double notificationHeight = notificationStage.getHeight();

            notificationStage.setX(main.getX() + screenWidth - notificationWidth - 20);
            notificationStage.setY(main.getY() + screenHeight - notificationHeight - 50);
        });
    }

    /**
     * Creates a notification scene.
     * @param text The description on the notification with updated font and size.
     * @return created notification Scene.
     */
    public static Scene createScene(Text text) {
        StackPane root = new StackPane(text);
        root.getStyleClass().add("notification");
        root.getStyleClass().add(switch (Notification.type) {
            case ERROR -> "notification-error";
            case INFO -> "notification-info";
        });

        Scene scene = new Scene(root);
        scene.setFill(Color.TRANSPARENT);
        scene.getStylesheets().add(Notification.class.
                getResource("/components/notification.css").toExternalForm());
        return scene;
    }
}
