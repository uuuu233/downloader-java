module uu.downloaderjava {
    requires javafx.controls;
    requires javafx.fxml;

    requires org.controlsfx.controls;
    requires org.kordamp.ikonli.javafx;
    requires org.kordamp.bootstrapfx.core;
    requires com.sun.jna;
    requires com.sun.jna.platform;
    requires java.xml;
    requires static lombok;

    opens uu.downloader to javafx.fxml;
    exports uu.downloader;
}