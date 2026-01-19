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
                JsonNode jsonNode = mapper.readTree("{}");
                System.out.println(jsonNode);
                HttpClient client = HttpClient.newBuilder()
                        .connectTimeout(Duration.ofSeconds(10))  // 连接超时
                        .followRedirects(HttpClient.Redirect.NORMAL)  // 允许重定向
                        .build();

                // 2. 构建 GET 请求
                HttpRequest request = HttpRequest.newBuilder()
                        .uri(URI.create("https://httpbin.org/get?name=test"))  // 请求URL
                        .timeout(Duration.ofSeconds(5))  // 请求超时
                        .GET()  // 默认就是GET，可省略
                        .build();

                // 3. 发送请求并获取响应（同步阻塞）

                HttpResponse<String> response = client.send(
                        request,
                        HttpResponse.BodyHandlers.ofString()  // 响应体以字符串接收
                );

                // 4. 处理响应
                System.out.println("响应状态码：" + response.statusCode());
                System.out.println("响应头：" + response.headers());
                System.out.println("响应体：" + response.body());

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
