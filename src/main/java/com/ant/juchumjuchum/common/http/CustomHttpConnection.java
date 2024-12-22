package com.ant.juchumjuchum.common.http;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Map;
import org.springframework.stereotype.Component;

@Component
public class CustomHttpConnection {

    public InputStream downloadFileStream(String downloadUrl) throws IOException, URISyntaxException {
        HttpURLConnection connection = createDownloadConnection(downloadUrl);
        validateStatus(downloadUrl, connection);
        return connection.getInputStream();
    }

    public Object postRequest(String url, Map<String, String> body, Class<?> clazz)
            throws IOException, URISyntaxException, InterruptedException {
        HttpClient client = HttpClient.newHttpClient();
        ObjectMapper objectMapper = new ObjectMapper();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(new URI(url))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(objectMapper.writeValueAsString(body)))
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        if (response.statusCode() != HttpURLConnection.HTTP_OK) {
            throw new IOException("Failed to post request to " + url);
        }
        return objectMapper.registerModule(new JavaTimeModule()).readValue(response.body(), clazz);
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
