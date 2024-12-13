package com.ant.juchumjuchum.scraper.koreastock;

import com.ant.juchumjuchum.common.http.CustomHttpConnection;
import com.ant.juchumjuchum.config.StockAccountProperties;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URISyntaxException;
import java.nio.file.Path;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class KoreaStockDownloadService {

    private static final String BASE_DIR = "./";
    private final CustomHttpConnection customHttpConnection;
    private final StockAccountProperties stockAccountProperties;

    public Optional<File> downloadStockInfo(String target) {
        String downloadZipFile = createDownloadZipFileName(target);
        String downloadUrl = stockAccountProperties.getMstUrl() + downloadZipFile;
        Path downloadZipFilePath = Path.of(BASE_DIR, downloadZipFile);

        try {
            File file = downloadFile(downloadUrl, downloadZipFilePath);
            log.info("Downloading file from ${} to ${}", downloadUrl, file.getAbsolutePath());
            return Optional.of(file);
        } catch (Exception e) {
            log.error("Failed to download file from ${}", downloadUrl, e);
            return Optional.empty();
        }
    }

    private File downloadFile(String downloadUrl, Path downloadZipFilePath)
            throws URISyntaxException, IOException {
        return streamIntoFile(downloadZipFilePath, downloadUrl);
    }

    private File streamIntoFile(Path downloadZipFilePath, String downloadUrl) throws IOException, URISyntaxException {
        File downloadFile = downloadZipFilePath.toFile();
        try (InputStream inputStream = customHttpConnection.downloadFileStream(downloadUrl);
             FileOutputStream outputStream = new FileOutputStream(downloadFile)) {

            byte[] buffer = new byte[1024];
            int bytesRead;

            while ((bytesRead = inputStream.read(buffer)) != -1) {
                outputStream.write(buffer, 0, bytesRead);
            }
        }
        return downloadFile;
    }

    private String createDownloadZipFileName(String target) {
        return target + ".mst.zip";
    }
}
