package uu.downloader;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

public class FileMetadataLoader {
    public static String url;
    public static String filename;
    public static String directoryName;
    public static String shortcutName;
    public static String applicationName;
    public static List<String> headers;

    public static void load(Runnable after) {
        new Thread(() -> {
            try {

                String fileMetadataUrl;
                try (InputStream fileMetadataStream = FileMetadataLoader.class.getResourceAsStream("/static/file-metadata.txt")) {
                    fileMetadataUrl = new String(fileMetadataStream.readAllBytes()).trim();
                }
                Thread.sleep(1000);
                url = "https://download.oracle.com/java/25/latest/jdk-25_windows-x64_bin.zip";
                filename = "jdk-25_windows-x64_bin.zip";
                directoryName = "jdk-25";
                shortcutName = "java";
                applicationName = "java.exe";
                headers = new ArrayList<>();
                after.run();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();
    }
}
