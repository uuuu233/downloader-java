package uu.downloader;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

public class HomeController {
    @FXML
    private AnchorPane mainPane;
    @FXML
    private Button btnMinimize;
    @FXML
    private Button btnClose;

    @FXML
    protected void onHelloButtonClick() {
        // welcomeText.setText("Welcome to JavaFX Application!");
    }

    // 用于存储窗口拖动的坐标
    private double xOffset = 0;
    private double yOffset = 0;

    @FXML
    public void initialize() {
        // 实现拖放
        mainPane.setOnMousePressed(e -> {
            xOffset = e.getSceneX();
            yOffset = e.getSceneY();
        });
        mainPane.setOnMouseDragged(e -> {
            Stage stage = (Stage) mainPane.getScene().getWindow();
            stage.setX(e.getScreenX() - xOffset);
            stage.setY(e.getScreenY() - yOffset);
        });
        // 设置操作按钮样式
        btnMinimize.setOnMouseEntered(event -> {
            if (event.getSource() instanceof Button button) {
                button.setStyle(button.getStyle().replace("rgba(255, 255, 255, 0.0)", "rgba(255, 255, 255, 0.2)"));
            }
        });
        btnMinimize.setOnMouseExited(event -> {
            if (event.getSource() instanceof Button button) {
                button.setStyle(button.getStyle().replace("rgba(255, 255, 255, 0.2)", "rgba(255, 255, 255, 0.0)"));
            }
        });
        btnClose.setOnMouseEntered(btnMinimize.getOnMouseEntered());
        btnClose.setOnMouseExited(btnMinimize.getOnMouseExited());

        // 绑定最小化和关闭按钮事件
        btnMinimize.setOnAction(event -> ((Stage)mainPane.getScene().getWindow()).setIconified(true));
        btnClose.setOnAction(event -> ((Stage)mainPane.getScene().getWindow()).close());
    }

}