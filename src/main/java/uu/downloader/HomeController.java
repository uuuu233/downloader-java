package uu.downloader;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.stage.DirectoryChooser;
import javafx.stage.Stage;

import java.io.File;

public class HomeController {
    @FXML
    private AnchorPane mainPane;
    @FXML
    private Button btnMinimize;
    @FXML
    private Button btnClose;
    @FXML
    private Button btnDownload;
    @FXML
    private Button btnSelectDownload;
    @FXML
    private Button btnSelectInstall;
    @FXML
    private TextField downloadPath;

    @FXML
    protected void onHelloButtonClick() {
        // welcomeText.setText("Welcome to JavaFX Application!");
    }

    // 用于存储窗口拖动的坐标
    private double xOffset = 0;
    private double yOffset = 0;

    @FXML
    public void initialize() {
        btnDownload.setDisable(true);
        // 加载文件元信息, 再激活下载按钮
        FileMetadataLoader.load(() -> Platform.runLater(() -> btnDownload.setDisable(false)));
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

        // 设置 更新窗口控制按钮样式
        btnMinimize.setOnMouseEntered(this::updateWindowControlButtonStyle);
        btnMinimize.setOnMouseExited(this::updateWindowControlButtonStyle);
        btnClose.setOnMouseEntered(this::updateWindowControlButtonStyle);
        btnClose.setOnMouseExited(this::updateWindowControlButtonStyle);

        // 绑定最小化和关闭按钮事件
        btnMinimize.setOnAction(event -> ((Stage)mainPane.getScene().getWindow()).setIconified(true));
        btnClose.setOnAction(event -> ((Stage)mainPane.getScene().getWindow()).close());

        // 下载按钮
        btnDownload.setOnAction(this::download);
        btnSelectDownload.setOnAction(this::selectDownloadDirectory);
        btnSelectInstall.setOnAction(this::selectInstallDirectory);
    }

    private void download(ActionEvent event) {

    }

    /**
     * 更新窗口控制按钮样式
     */
    private void updateWindowControlButtonStyle(MouseEvent event) {
        if (event.getSource() instanceof Button button) {
            String eventTypeName = event.getEventType().getName();
            if (eventTypeName.equals("MOUSE_ENTERED")) {
                button.setStyle(button.getStyle().replace("rgba(255, 255, 255, 0.0)", "rgba(255, 255, 255, 0.2)"));
            } else if (eventTypeName.equals("MOUSE_EXITED")) {
                button.setStyle(button.getStyle().replace("rgba(255, 255, 255, 0.2)", "rgba(255, 255, 255, 0.0)"));
            }
        }
    }

    private void selectDownloadDirectory(ActionEvent event) {
        DirectoryChooser directoryChooser = new DirectoryChooser();
        directoryChooser.setTitle("选择下载目录");
        /*File file = new File(downloadPath.getText());
        if (file.exists()) {
            directoryChooser.setInitialDirectory(file);
        }*/
        Stage stage = (Stage) mainPane.getScene().getWindow();
        File selectedDirectory = directoryChooser.showDialog(stage);
        if (selectedDirectory != null) {
            downloadPath.setText(selectedDirectory.getAbsolutePath());
        }
    }

    private void selectInstallDirectory(ActionEvent event) {
        DirectoryChooser directoryChooser = new DirectoryChooser();
        directoryChooser.setTitle("选择安装目录");
        /*File file = new File(downloadPath.getText());
        if (file.exists()) {
            directoryChooser.setInitialDirectory(file);
        }*/
        Stage stage = (Stage) mainPane.getScene().getWindow();
        File selectedDirectory = directoryChooser.showDialog(stage);
        if (selectedDirectory != null) {
            downloadPath.setText(selectedDirectory.getAbsolutePath());
        }
    }

}