package uu.downloader.util;

import com.fasterxml.jackson.databind.JsonNode;
import lombok.SneakyThrows;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.time.Duration;

public class HttpClientUtil {
    private static final HttpClient httpclient = HttpClient.newBuilder().build();

    public static void get(String url) {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("https://httpbin.org/get?name=test"))  // 请求URL
                .timeout(Duration.ofSeconds(5))  // 请求超时
                .GET()  // 默认就是GET，可省略
                .build();
    }

    @SneakyThrows
    public static JsonNode post(String url, String body) {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url)) // 替换为你的目标 URL
                .header("Content-Type", "application/json") // 指定请求体格式为 JSON
                .header("Accept", "application/json") // 期望返回 JSON
                .POST(HttpRequest.BodyPublishers.ofString(body, StandardCharsets.UTF_8)) // POST 方法 + 请求体
                .build();
        HttpResponse<String> response = httpclient.send(request, HttpResponse.BodyHandlers.ofString());
        return JsonUtil.mapper.readTree(response.body());
        // 根据状态码判断请求是否成功
        // aria2c remove 响应 http status 400
        /*if (response.statusCode() >= 200 && response.statusCode() < 300) {
            return JsonUtil.mapper.readTree(response.body());
        } else {
            throw new RuntimeException();
        }*/
    }
}
