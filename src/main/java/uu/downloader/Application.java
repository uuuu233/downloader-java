package uu.downloader;

import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.io.IOException;

public class Application extends javafx.application.Application {
    @Override
    public void start(Stage stage) throws IOException {
        // 设置无边框 标题 可改大小
        stage.initStyle(StageStyle.TRANSPARENT);
        stage.setTitle("");
        stage.setResizable(false);

        // 设置背景图片
        ImageView backgroundView = new ImageView(new Image(Application.class.getResourceAsStream("/static/background.png")));
        backgroundView.setFitWidth(600);
        backgroundView.setFitHeight(400);
        backgroundView.setPreserveRatio(false);

        Pane root = FXMLLoader.load(Application.class.getResource("/view/home.fxml"));
        StackPane homePane = new StackPane();
        homePane.getChildren().addAll(backgroundView, root);
        Scene scene = new Scene(homePane, 600, 400);
        stage.setScene(scene);

        // 设置栈面板样式，实现圆角效果
        /*homePane.setStyle("""
                -fx-background-radius: 20; 
                -fx-background-color: transparent; 
                -fx-border-radius: 20; 
                -fx-border-color: transparent;
                """);*/
        // 设置栈面板样式，实现圆角效果
        // homePane.setStyle("-fx-background-radius: 20; -fx-background-color: transparent; -fx-border-radius: 20; -fx-border-color: transparent;");

        Rectangle clip = new Rectangle(600, 400);
        clip.setArcWidth(20);
        clip.setArcHeight(20);
        homePane.setClip(clip);

        scene.setFill(Color.TRANSPARENT);
        stage.show();
    }

    public static void main(String[] args) {
        System.out.println(Application.class.getResource("hello-view.fxml"));
        launch();
    }
}