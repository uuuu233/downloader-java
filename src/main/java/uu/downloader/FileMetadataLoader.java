package uu.downloader;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.json.JsonMapper;

import java.io.InputStream;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;

public class FileMetadataLoader {
    public static JsonMapper mapper = JsonMapper.builder()
            .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
            .configure(DeserializationFeature.FAIL_ON_NULL_FOR_PRIMITIVES, false)
            .build();
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
                Thread.sleep(1000);
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
