package uu.downloader;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.json.JsonMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import uu.downloader.util.HttpClientUtil;
import uu.downloader.util.JsonUtil;
import uu.downloader.util.ZipUtil;

import java.io.IOException;
import java.io.InputStream;
import java.net.ServerSocket;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

public class Aria2c {
    private static final Path aria2cPath = Path.of(System.getenv("APPDATA"), "uu", "aria2c", "aria2c.exe");
    public static String address;
    public static final String requestId = "uu" + System.currentTimeMillis() / 1000;
    public static String downloadJobId;
    public static JsonMapper mapper = JsonUtil.mapper;

    public static void init() throws IOException {
        if (!Files.exists(aria2cPath)) {
            try (InputStream resourceAsStream = Aria2c.class.getResourceAsStream("/static/aria2c.exe")) {
                ZipUtil.unzip(resourceAsStream, aria2cPath);
            }
        }
        ServerSocket serverSocket = new ServerSocket(0);
        int port = serverSocket.getLocalPort();
        serverSocket.close();

        List<String> args = List.of(
                "--enable-rpc",
                "--rpc-listen-port=" + port,
                "--file-allocation=trunc",
                "--check-certificate=false",
                "-x 8",
                "-s 8",
                "-c");
        System.out.println(String.join(" ", args));
        // WindowsJobObject.CreateProcessAndSetJobObject(aria2cPath.normalize() + " " + String.join(" ", args));
        address = "http://localhost:" + port + "/jsonrpc";
        address = "http://localhost:1688/jsonrpc";
        System.out.println(address);
    }

    public static void addUrl(String url, String destDirectory, String destFileName, List<String> headers) {
        if (!(downloadJobId == null || downloadJobId.isEmpty())) {
            throw new IllegalStateException();
        }
        // 开始请求下载
        ObjectNode objectNode = mapper.createObjectNode();
        objectNode.put("id", Aria2c.requestId);
        objectNode.put("jsonrpc", "2.0");
        objectNode.put("method", "aria2.addUri");
        ArrayNode paramsNode = mapper.createArrayNode()
                .add(mapper.createArrayNode().add(url))
                .add(mapper.createObjectNode().put("dir", destDirectory).put("out", destFileName));
        if (!headers.isEmpty()) {
            ArrayNode headersNode = mapper.createArrayNode();
            for (String header : headers) {
                headersNode.add(header);
            }
        }
        objectNode.set("params", paramsNode);
        JsonNode result = HttpClientUtil.post(Aria2c.address, objectNode.toString());
        downloadJobId = result.path("result").asText();
        if (downloadJobId == null || downloadJobId.isEmpty()) {
            throw new IllegalStateException();
        }
    }

    public static DownloadJobInfo tellStatus() {
        ObjectNode bodyNode = mapper.createObjectNode();
        bodyNode.put("id", Aria2c.requestId);
        bodyNode.put("jsonrpc", "2.0");
        bodyNode.put("method", "aria2.tellStatus");
        bodyNode.set("params", mapper.createArrayNode().add(downloadJobId));
        JsonNode responseNode = HttpClientUtil.post(Aria2c.address, bodyNode.toString());
        JsonNode resultNode = responseNode.path("result");
        long completedLength = Long.parseLong(resultNode.path("completedLength").asText());
        long totalLength = Long.parseLong(resultNode.path("totalLength").asText());
        long speed = Long.parseLong(resultNode.path("downloadSpeed").asText());
        String speedDescription = convertSpeedToDescription(speed);
        DownloadJobInfo downloadJobInfo = new DownloadJobInfo();
        downloadJobInfo.status = resultNode.path("status").asText();
        downloadJobInfo.completedLength = completedLength;
        downloadJobInfo.totalLength = totalLength;
        downloadJobInfo.speed = speed;
        downloadJobInfo.speedDescription = speedDescription;
        downloadJobInfo.progress = completedLength / (double)totalLength;
        downloadJobInfo.progressDescription = (int)(downloadJobInfo.progress * 100) + "%";
        downloadJobInfo.message = resultNode.path("message").asText();
        return downloadJobInfo;
    }

    public static void remove() {
        if (downloadJobId == null || downloadJobId.isEmpty()) {
            return;
        }
        ObjectNode bodyNode = mapper.createObjectNode();
        bodyNode.put("id", Aria2c.requestId);
        bodyNode.put("jsonrpc", "2.0");
        bodyNode.put("method", "aria2.remove");
        bodyNode.set("params", mapper.createArrayNode().add(downloadJobId));
        HttpClientUtil.post(Aria2c.address, bodyNode.toString());
        downloadJobId = null;
    }

    public static class DownloadJobInfo {
        public String status;
        public long completedLength;
        public long totalLength;
        public long speed;
        public String speedDescription;
        public double progress;
        public String progressDescription;
        public String message;
    }

    private static String convertSpeedToDescription(long speed) {
        String speedText;
        if (speed < 1) {
            speedText = "0KB/s";
        } else if (speed < 1048576) {
            speedText = String.format("%.2fKB/s", (speed / 1024D));
        } else if (speed < 1073741824) {
            speedText = String.format("%.2fMB/s", (speed / 1048576D));
        } else {
            speedText = String.format("%.2fGB/s", (speed / 1073741824D));
        }
        return speedText;
    }

}
