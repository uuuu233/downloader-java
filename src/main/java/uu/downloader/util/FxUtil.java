package uu.downloader.util;

import javafx.scene.control.Alert;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;

import java.util.Optional;

public class FxUtil {
    public static void showOkAlert(String content) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle(null);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.getButtonTypes().setAll(new ButtonType("好的", ButtonBar.ButtonData.CANCEL_CLOSE));
        // 显示对话框并等待用户响应
        alert.showAndWait();
    }

    public static boolean showYesNoAlert(String content) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle(null);
        alert.setHeaderText(null);
        alert.setContentText(content);
        ButtonType yes = new ButtonType("是", ButtonBar.ButtonData.YES);
        ButtonType no = new ButtonType("否", ButtonBar.ButtonData.CANCEL_CLOSE);
        alert.getButtonTypes().setAll(yes, no);
        Optional<ButtonType> buttonType = alert.showAndWait();
        return "是".equals(buttonType.orElse(no).getText());
    }

}
