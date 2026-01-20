package uu.downloader;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.json.JsonMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.stage.DirectoryChooser;
import javafx.stage.Stage;
import lombok.Getter;
import lombok.SneakyThrows;
import uu.downloader.util.HttpClientUtil;
import uu.downloader.util.JsonUtil;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

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
    private TextField installPath;
    @Getter
    @FXML
    private ProgressBar progressBar;
    @Getter
    @FXML
    private Label progressLabel;
    @FXML
    private CheckBox chkAutoInstall;
    @FXML
    private CheckBox chkCreateShortcut;

    // 用于存储窗口拖动的坐标
    private double xOffset = 0;
    private double yOffset = 0;
    private String lastDownloadButtonText;

    @FXML
    public void initialize() {
        progressLabel.setText("初始化中");
        btnDownload.setDisable(true);
        // 加载文件元信息, 再激活下载按钮
        FileMetadataLoader.load(() -> Platform.runLater(() -> {
            btnDownload.setDisable(false);
            progressLabel.setText("0%");
        }));
        // 实现拖放
        mainPane.setOnMousePressed(this::windowDrag);
        mainPane.setOnMouseDragged(this::windowDrag);

        // 设置 窗口控制按钮样式
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

    @SneakyThrows
    private boolean checkInstallDirectory() {
        try {
            Path path = Path.of(installPath.getText());
            if (!Files.exists(path)) {
                Files.createDirectories(path);
                return true;
            } else if (!Files.isDirectory(path) || Objects.requireNonNullElse(path.toFile().list(), new File[0]).length != 0) {
                throw new RuntimeException();
            } else {
                return true;
            }
        } catch (Exception e) {
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("警告");
            alert.setHeaderText(null);
            alert.setContentText("安装目录必须为空");
            alert.getButtonTypes().setAll(new ButtonType("好的"));
            // 显示对话框并等待用户响应
            alert.showAndWait();
            return false;
        }
    }

    @SneakyThrows
    private boolean checkDownloadDirectory() {
        try {
            Path path = Path.of(downloadPath.getText());
            if (!Files.exists(path)) {
                Files.createDirectories(path);
                return true;
            } else if (!Files.isDirectory(path)) {
                throw new RuntimeException();
            } else {
                return true;
            }
        } catch (Exception e) {
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("警告");
            alert.setHeaderText(null);
            alert.setContentText("无法下载到这个目录");
            alert.getButtonTypes().setAll(new ButtonType("好的"));
            // 显示对话框并等待用户响应
            alert.showAndWait();
            return false;
        }
    }

    private void download(ActionEvent event) {
        if (btnDownload.getText().equals("下载")) {
            if (chkAutoInstall.isSelected() && !checkInstallDirectory()) {
                return;
            } else if (!checkDownloadDirectory()) {
                return;
            }
            Path path = Path.of(downloadPath.getText(), FileMetadataLoader.filename);
            Path pathAria2 = Path.of(downloadPath.getText(), FileMetadataLoader.filename + ".aria2");
            if (Files.exists(path) && !Files.exists(pathAria2)) {
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("警告");
                alert.setHeaderText(null);
                alert.setContentText("检测到已经下载完成, 是否直接安装?");
                alert.getButtonTypes().setAll(new ButtonType("是", ButtonBar.ButtonData.YES),
                        new ButtonType("否", ButtonBar.ButtonData.CANCEL_CLOSE));
                // 显示对话框并等待用户响应
                Optional<ButtonType> buttonType = alert.showAndWait();
                ButtonType clickButton = buttonType.orElse(ButtonType.CANCEL);
                if ("否".equals(clickButton.getText())) {
                    return;
                }
            }
            this.download(FileMetadataLoader.url, downloadPath.getText(), FileMetadataLoader.filename, FileMetadataLoader.headers, () -> {
                if (chkAutoInstall.isSelected()) {
                    install("", "");
                }
            });
        } else if (btnDownload.getText().equals("安装")) {
            if (!checkInstallDirectory()) {
                return;
            }
        } else if (btnDownload.getText().equals("取消")) {

        }
        if (btnDownload.getText().equals("下载")) {
            lastDownloadButtonText = btnDownload.getText();
            btnDownload.setText("取消");
            downloadPath.setDisable(true);
            btnSelectDownload.setDisable(true);
            btnSelectInstall.setDisable(false);
            installPath.setDisable(false);
        } else if (btnDownload.getText().equals("安装")) {
            lastDownloadButtonText = btnDownload.getText();
            btnDownload.setText("取消");
            downloadPath.setDisable(false);
            btnSelectDownload.setDisable(false);
            btnSelectInstall.setDisable(true);
            installPath.setDisable(true);
        } else if (btnDownload.getText().equals("取消")) {
            downloadPath.setDisable(false);
            btnSelectDownload.setDisable(false);
            btnSelectInstall.setDisable(false);
            installPath.setDisable(false);
            btnDownload.setText(lastDownloadButtonText);
        }
    }

    /**
     * 窗口拖动
     */
    private void windowDrag(MouseEvent event) {
        String eventTypeName = event.getEventType().getName();
        if (eventTypeName.equals("MOUSE_PRESSED")) {
            xOffset = event.getSceneX();
            yOffset = event.getSceneY();
        } else if (eventTypeName.equals("MOUSE_DRAGGED")) {
            Stage stage = (Stage) mainPane.getScene().getWindow();
            stage.setX(event.getScreenX() - xOffset);
            stage.setY(event.getScreenY() - yOffset);
        }
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
        Stage stage = (Stage) mainPane.getScene().getWindow();
        File selectedDirectory = directoryChooser.showDialog(stage);
        if (selectedDirectory != null) {
            downloadPath.setText(selectedDirectory.getAbsolutePath());
        }
    }

    private void selectInstallDirectory(ActionEvent event) {
        DirectoryChooser directoryChooser = new DirectoryChooser();
        directoryChooser.setTitle("选择安装目录");
        Stage stage = (Stage) mainPane.getScene().getWindow();
        File selectedDirectory = directoryChooser.showDialog(stage);
        if (selectedDirectory != null) {
            String path = selectedDirectory.getAbsolutePath();
            if (!(path.endsWith("/") || path.endsWith("\\"))) {
                path = path + File.separator;
            }
            path += FileMetadataLoader.directoryName;
            installPath.setText(path);
        }
    }

    private void install(String source, String targetDirectory) {

    }

    private void download(final String url, final String destDirectory, final String destFileName, final List<String> headers, final Runnable installRunnable) {
        new Thread(() -> {
            try {
                JsonMapper mapper = JsonUtil.mapper;
                String downloadJobId;
                ObjectNode objectNode = mapper.createObjectNode();
                objectNode.put("id", Aria2c.requestId);
                objectNode.put("jsonrpc", "2.0");
                objectNode.put("method", "aria2.addUri");
                ArrayNode paramsNode = mapper.createArrayNode()
                        .add(mapper.createArrayNode().add(url))
                        .add(mapper.createObjectNode().put("dir", destDirectory).put("out", destFileName));
                if (!headers.isEmpty()) {
                    ArrayNode headersNode = mapper.createArrayNode();
                    for (String header : headers) {
                        headersNode.add(header);
                    }
                }
                objectNode.set("params", paramsNode);
                JsonNode result = HttpClientUtil.post(Aria2c.address, objectNode.toString());
                if ((downloadJobId = result.path("result").asText()).isEmpty()) {
                    Platform.runLater(() -> progressLabel.setText("下载出现未知错误"));
                    throw new RuntimeException();
                }
                for (;;) {
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                    ObjectNode bodyNode = mapper.createObjectNode();
                    bodyNode.put("id", Aria2c.requestId);
                    bodyNode.put("jsonrpc", "2.0");
                    bodyNode.put("method", "aria2.tellStatus");
                    bodyNode.set("params", mapper.createArrayNode().add(downloadJobId));
                    JsonNode responseNode = HttpClientUtil.post(Aria2c.address, bodyNode.toString());
                    System.out.println(responseNode.toPrettyString());
                    JsonNode resultNode = responseNode.path("result");
                    String status = resultNode.path("status").asText();
                    long completedLength = Long.parseLong(resultNode.path("completedLength").asText());
                    long totalLength = Long.parseLong(resultNode.path("totalLength").asText());
                    long speed = Long.parseLong(resultNode.path("downloadSpeed").asText());
                    String speedText;
                    if (speed < 1) {
                        speedText = "0KB/s";
                    } else if (speed < 1048576) {
                        speedText = String.format("%.2fKB/s", (speed / 1024D));
                    } else if (speed < 1073741824) {
                        speedText = String.format("%.2fMB/s", (speed / 1048576D));
                    } else {
                        speedText = String.format("%.2fGB/s", (speed / 1073741824D));
                    }
                    double progress = (completedLength / (double)totalLength);
                    if ("waiting".equals(status) || "active".equals(status) || "complete".equals(status)) {
                        Platform.runLater(() -> {
                            progressLabel.setText((int) (progress * 100) + "%  " + speedText);
                            progressBar.setProgress(progress);
                        });
                        if ("complete".equals(status)) {
                            System.out.println("下载已完成");
                            break;
                        }
                    } else if ("error".equals(status)) {
                        progressLabel.setText("下载出现错误: " + resultNode.path("errorMessage").asText());
                        throw new RuntimeException();
                    } else if ("removed".equals(status)) {
                        System.out.println("下载任务 removed");
                        break;
                    }
                }
            } finally {
                Platform.runLater(() -> btnDownload.setText("下载"));
                if (progressBar.getProgress() >= 1 && chkAutoInstall.isSelected()) {
                    installRunnable.run();
                }
            }
        }).start();
    }
}