package uu.downloader;

import org.apache.commons.compress.archivers.ArchiveEntry;
import org.apache.commons.compress.archivers.zip.ZipArchiveInputStream;
import org.apache.commons.io.IOUtils;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;

public class ZipUtil {

    public static void unzip(InputStream inputStream, Path dest) throws IOException {
        if (!Files.exists(dest)) {
            Files.createDirectories(dest);
        }
        try (ZipArchiveInputStream zis = new ZipArchiveInputStream(inputStream)) {
            ArchiveEntry entry;
            // 遍历ZIP中的每个条目（文件/文件夹）
            while ((entry = zis.getNextEntry()) != null) {
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
    }
}
