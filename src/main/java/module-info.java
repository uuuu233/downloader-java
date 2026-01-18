module uu.downloaderjava {
    requires javafx.controls;
    requires javafx.fxml;

    requires com.sun.jna;
    requires com.sun.jna.platform;
    requires java.xml;
    requires static lombok;

    opens uu.downloader to javafx.fxml;
    exports uu.downloader;
}