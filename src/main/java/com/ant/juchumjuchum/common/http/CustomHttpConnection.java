package com.ant.juchumjuchum.common.http;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import org.springframework.stereotype.Component;

@Component
public class CustomHttpConnection {

    public InputStream downloadFileStream(String downloadUrl) throws IOException, URISyntaxException {
        HttpURLConnection connection = createDownloadConnection(downloadUrl);
        validateStatus(downloadUrl, connection);
        return connection.getInputStream();
    }


    private void validateStatus(String downloadUrl, HttpURLConnection connection) throws IOException {
        if (connection.getResponseCode() != HttpURLConnection.HTTP_OK) {
            throw new IOException("Failed to download file from " + downloadUrl);
        }
    }

    private HttpURLConnection createDownloadConnection(String downloadUrl) throws IOException, URISyntaxException {
        URL url = new URI(downloadUrl).toURL();
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("GET");
        return connection;
    }
}
