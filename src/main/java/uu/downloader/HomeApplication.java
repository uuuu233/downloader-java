package uu.downloader;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.io.IOException;

public class HomeApplication extends Application {
    public static final Image logo = new Image(HomeApplication.class.getResourceAsStream("/static/icon.png"));
    @Override
    public void start(Stage stage) throws IOException {
        stage.getIcons().add(logo);
        // 设置无边框 标题 可改大小
        stage.initStyle(StageStyle.TRANSPARENT);
        stage.setTitle("");
        stage.setResizable(false);

        // 设置背景图片
        ImageView backgroundView = new ImageView(new Image(HomeApplication.class.getResourceAsStream("/static/background.png")));
        backgroundView.setFitWidth(600);
        backgroundView.setFitHeight(400);
        backgroundView.setPreserveRatio(false);

        Pane root = FXMLLoader.load(HomeApplication.class.getResource("/view/home.fxml"));
        StackPane homePane = new StackPane();
        homePane.getChildren().addAll(backgroundView, root);
        Scene scene = new Scene(homePane, 600, 400);
        stage.setScene(scene);

        Rectangle clip = new Rectangle(600, 400);
        clip.setArcWidth(20);
        clip.setArcHeight(20);
        homePane.setClip(clip);

        scene.setFill(Color.TRANSPARENT);
        stage.show();
        Aria2c.init();
    }
}