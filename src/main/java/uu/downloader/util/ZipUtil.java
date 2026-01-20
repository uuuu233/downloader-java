package uu.downloader.util;

import lombok.SneakyThrows;
import org.apache.commons.compress.archivers.ArchiveEntry;
import org.apache.commons.compress.archivers.zip.ZipArchiveInputStream;
import org.apache.commons.io.IOUtils;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.function.BiConsumer;

public class ZipUtil {

    @SneakyThrows
    public static void unzip(Path source, Path dest, BiConsumer<String, Long> zipping) {
        try (FileInputStream fileInputStream = new FileInputStream(source.toFile())) {
            unzip(fileInputStream, dest, zipping);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @SneakyThrows
    public static void unzip(Path source, Path dest) {
        unzip(source, dest, null);
    }

    public static void unzip(InputStream inputStream, Path dest) {
        unzip(inputStream, dest, null);
    }

    @SneakyThrows
    public static void unzip(InputStream inputStream, Path dest, BiConsumer<String, Long> zipping) {
        try {
            if (!Files.exists(dest)) {
                Files.createDirectories(dest);
            }
            try (ZipArchiveInputStream zis = new ZipArchiveInputStream(inputStream)) {
                ArchiveEntry entry;
                // 遍历ZIP中的每个条目（文件/文件夹）
                while ((entry = zis.getNextEntry()) != null) {
                    if (Thread.interrupted()) {
                        throw new InterruptedException();
                    }
                    if (zipping != null) {
                        zipping.accept(entry.getName(), entry.getSize());
                    }
                    Path entryPath = dest.resolve(entry.getName());

                    // 处理文件夹
                    if (entry.isDirectory()) {
                        Files.createDirectories(entryPath);
                        continue;
                    }

                    // 处理文件（自动创建父目录）
                    Files.createDirectories(entryPath.getParent());
                    try (OutputStream out = Files.newOutputStream(entryPath)) {
                        IOUtils.copy(zis, out);
                    }
                }
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

}
