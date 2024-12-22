package client.utils;

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
    public static void makeText(Stage main, String description)
    {
        int messageTime = 2000;
        int fadeIn = 500;
        int fadeOut = 500;

        Stage notificatonStage=new Stage();
        notificatonStage.initOwner(main);
        notificatonStage.setResizable(false);
        notificatonStage.initStyle(StageStyle.TRANSPARENT);

        Text text = new Text(description);
        text.setFont(Font.font("Bold", 18));
        text.setFill(Color.WHITESMOKE);

        StackPane root = new StackPane(text);
        root.setStyle("-fx-background-radius: 10; " +
                "-fx-background-color: #DB4455; " +
                "-fx-border-color: rgba(255, 255, 255, 0.6); " +
                "-fx-border-width: 1px; " +
                "-fx-border-radius: 10; " +
                "-fx-padding: 25px; " +
                "-fx-effect: dropshadow(gaussian, rgba(0, 0, 0, 0.5), 10, 0, 0, 4);");
        root.setOpacity(0);

        Scene scene = new Scene(root);
        scene.setFill(Color.TRANSPARENT);
        notificatonStage.setScene(scene);

        notificatonStage.setOnShown(event -> {
            double screenWidth = main.getScene().getWindow().getWidth();
            double screenHeight = main.getScene().getWindow().getHeight();
            double notificationWidth = notificatonStage.getWidth();
            double notificationHeight = notificatonStage.getHeight();

            notificatonStage.setX(main.getX() + screenWidth - notificationWidth - 20);
            notificatonStage.setY(main.getY() + screenHeight - notificationHeight - 50);
        });

        notificatonStage.show();

        Timeline fadeInTimeline = new Timeline();
        KeyFrame fadeInKey1 = new KeyFrame(Duration.millis(fadeIn), new KeyValue (notificatonStage.getScene().getRoot().opacityProperty(), 1));
        fadeInTimeline.getKeyFrames().add(fadeInKey1);
        fadeInTimeline.setOnFinished((ae) ->
        {
            new Thread(() -> {
                try
                {
                    Thread.sleep(messageTime);
                }
                catch (InterruptedException e)
                {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
                Timeline fadeOutTimeline = new Timeline();
                KeyFrame fadeOutKey1 = new KeyFrame(Duration.millis(fadeOut), new KeyValue (notificatonStage.getScene().getRoot().opacityProperty(), 0));
                fadeOutTimeline.getKeyFrames().add(fadeOutKey1);
                fadeOutTimeline.setOnFinished((aeb) -> notificatonStage.close());
                fadeOutTimeline.play();
            }).start();
        });
        fadeInTimeline.play();
    }
}
