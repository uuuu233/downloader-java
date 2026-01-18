package uu.downloader;

import java.io.InputStream;

public class FileMetadataLoader {
    public static String url;
    public static String filename;
    public static String directoryName;
    public static String shortcutName;
    public static String applicationName;
    public static String headers;

    public static void load(Runnable after) {
        new Thread(() -> {
            try {
                String fileMetadataUrl;
                try (InputStream fileMetadataStream = FileMetadataLoader.class.getResourceAsStream("/static/file-metadata.txt")) {
                    fileMetadataUrl = new String(fileMetadataStream.readAllBytes()).trim();
                }
                Thread.sleep(2000);
                url = "https://download.oracle.com/java/25/latest/jdk-25_windows-x64_bin.zip";
                filename = "jdk-25_windows-x64_bin.zip";
                directoryName = "jdk-25";
                shortcutName = "java";
                applicationName = "java.exe";
                headers = "";
                after.run();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();
    }
}
