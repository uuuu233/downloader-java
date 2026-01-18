package uu.downloader;

import lombok.SneakyThrows;

import java.io.IOException;
import java.io.InputStream;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

public class Aria2c {
    private static final Path aria2cPath = Path.of(System.getenv("APPDATA"), "uu", "aria2c", "aria2c.exe");
    private static String address;

    public static void start() throws IOException {
        if (!Files.exists(aria2cPath)) {
            try (InputStream resourceAsStream = Aria2c.class.getResourceAsStream("/static/aria2c.exe")) {
                Files.copy(resourceAsStream, aria2cPath);
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
        WindowsJobObject.CreateProcessAndSetJobObject(aria2cPath.normalize() + " " + String.join(" ", args));
        address = "http://localhost:" + port + "/jsonrpc";
    }

    public static void main(String[] args) throws IOException {
        start();
        System.out.println();
    }
}
