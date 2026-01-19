module uu.downloaderjava {
    requires javafx.controls;
    requires javafx.fxml;

    requires com.sun.jna;
    requires com.sun.jna.platform;
    requires static lombok;
    requires com.fasterxml.jackson.databind;
    requires java.net.http;
    requires org.apache.commons.compress;
    requires org.apache.commons.io;

    opens uu.downloader to javafx.fxml;
    exports uu.downloader;
}