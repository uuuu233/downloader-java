package uu.downloader;

import com.fasterxml.jackson.databind.JsonNode;
import uu.downloader.util.HttpClientUtil;
import uu.downloader.util.JsonUtil;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.util.*;

public class FileMetadata {
    public static String url;
    public static String filename;
    public static String directoryName;
    public static String shortcutName;
    public static String defaultInstallPath;
    public static String applicationName;
    public static List<String> headers;

    public static void load(Runnable after) {
        new Thread(() -> {
            try {
                String fileMetadataUrl;
                try (InputStream fileMetadataStream = FileMetadata.class.getResourceAsStream("/static/file-metadata.txt")) {
                    fileMetadataUrl = new String(fileMetadataStream.readAllBytes()).trim();
                }
                String docid = fileMetadataUrl.substring("https://docs.qq.com/markdown/".length(), !fileMetadataUrl.contains("?") ? fileMetadataUrl.length() : fileMetadataUrl.indexOf("?"));
                JsonNode resultNode = HttpClientUtil.get("https://docs.qq.com/dop-api/clientvar?id=" + docid, new HashMap<>(){{
                    put("Referer", fileMetadataUrl);
                }});
                String globalPadId = resultNode.at("/result/clientVars/collab_client_vars/globalPadId").asText();
                resultNode = HttpClientUtil.post("https://docs.qq.com/api/markdown/read/data", JsonUtil.mapper.createObjectNode().put("file_id", globalPadId).toString());
                String markdown = resultNode.at("/result/mark_down").asText();
                Properties properties = new Properties();
                try (InputStreamReader inputStream = new InputStreamReader(new ByteArrayInputStream(markdown.getBytes(StandardCharsets.UTF_8)))) {
                    properties.load(inputStream);
                }

                url = properties.getProperty("url");
                filename = properties.getProperty("filename");
                directoryName = properties.getProperty("directoryName");
                shortcutName = properties.getProperty("shortcutName");
                applicationName = properties.getProperty("applicationName");
                defaultInstallPath = properties.getProperty("defaultInstallPath");
                if (defaultInstallPath == null) {
                    defaultInstallPath = Path.of("d:\\Game", directoryName).toString();
                }
                headers = new ArrayList<>();
                if (properties.getProperty("headers") != null) {
                    String hs = properties.getProperty("headers").trim();
                    if (!hs.isEmpty()) {
                        headers.addAll(Arrays.asList(hs.split("\\^")));
                    }
                }
                after.run();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();
    }

    public static void main(String[] args) {
        System.out.println(Path.of("d:/Game", "asss").toString());
    }

}
