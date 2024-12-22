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
        int messageTime = 4000;
        int fadeIn = 500;
        int fadeOut = 500;

        Stage notificatonStage=new Stage();
        notificatonStage.initOwner(main);
        notificatonStage.setResizable(false);
        notificatonStage.initStyle(StageStyle.TRANSPARENT);

        Text text = new Text(description);
        text.setFont(Font.font("Verdana", 40));
        text.setFill(Color.RED);

        StackPane root = new StackPane(text);
        root.setStyle("-fx-background-radius: 20; -fx-background-color: rgba(0, 0, 0, 0.2); -fx-padding: 50px;");
        root.setOpacity(0);

        Scene scene = new Scene(root);
        scene.setFill(Color.TRANSPARENT);
        notificatonStage.setScene(scene);
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
