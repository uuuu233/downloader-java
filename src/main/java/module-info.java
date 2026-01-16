module uu.downloaderjava {
    requires javafx.controls;
    requires javafx.fxml;

    requires org.controlsfx.controls;
    requires org.kordamp.ikonli.javafx;
    requires org.kordamp.bootstrapfx.core;

    opens uu.downloaderjava to javafx.fxml;
    exports uu.downloaderjava;
}