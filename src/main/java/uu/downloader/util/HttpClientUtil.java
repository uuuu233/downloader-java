package uu.downloader.util;

import com.fasterxml.jackson.databind.JsonNode;
import lombok.SneakyThrows;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.util.Map;

public class HttpClientUtil {
    private static final HttpClient httpclient = HttpClient.newBuilder().build();

    public static JsonNode get(String url, Map<String, String> headers) {
        try {
            HttpRequest.Builder builder = HttpRequest.newBuilder()
                    .uri(URI.create(url))  // 请求URL
                    .GET();// 默认就是GET，可省略
            setHeaders(builder, headers);
            HttpRequest request = builder.build();
            HttpResponse<String> response = httpclient.send(request, HttpResponse.BodyHandlers.ofString());
            return JsonUtil.mapper.readTree(response.body());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

    }

    @SneakyThrows
    public static JsonNode post(String url, String body) {
        try {
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(url)) // 替换为你的目标 URL
                    .header("Content-Type", "application/json") // 指定请求体格式为 JSON
                    .header("Accept", "application/json") // 期望返回 JSON
                    .POST(HttpRequest.BodyPublishers.ofString(body, StandardCharsets.UTF_8)) // POST 方法 + 请求体
                    .build();
            HttpResponse<String> response = httpclient.send(request, HttpResponse.BodyHandlers.ofString());
            return JsonUtil.mapper.readTree(response.body());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @SneakyThrows
    public static JsonNode post(String url, String body, Map<String, String> headers) {
        try {
            HttpRequest.Builder builder = HttpRequest.newBuilder()
                    .uri(URI.create(url)) // 替换为你的目标 URL
                    .header("Content-Type", "application/json")
                    .header("Accept", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(body, StandardCharsets.UTF_8));
            setHeaders(builder, headers);
            HttpRequest request = builder.build();
            HttpResponse<String> response = httpclient.send(request, HttpResponse.BodyHandlers.ofString());
            return JsonUtil.mapper.readTree(response.body());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private static void setHeaders(HttpRequest.Builder builder, Map<String, String> headers) {
        if (headers != null) {
            for (Map.Entry<String, String> entry : headers.entrySet()) {
                builder.header(entry.getKey(), entry.getValue());
            }
        }
    }
}
