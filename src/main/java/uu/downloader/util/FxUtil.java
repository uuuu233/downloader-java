package uu.downloader.util;

import javafx.scene.control.Alert;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;
import javafx.stage.Stage;
import uu.downloader.HomeApplication;

import java.util.Optional;

public class FxUtil {
    public static void showOkAlert(String content) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle(null);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.getButtonTypes().setAll(new ButtonType("好的", ButtonBar.ButtonData.CANCEL_CLOSE));
        Stage stage = (Stage) alert.getDialogPane().getScene().getWindow();
        stage.getIcons().add(HomeApplication.logo);
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
        Stage stage = (Stage) alert.getDialogPane().getScene().getWindow();
        stage.getIcons().add(HomeApplication.logo);
        Optional<ButtonType> buttonType = alert.showAndWait();
        return "是".equals(buttonType.orElse(no).getText());
    }

}
