package com.ant.juchumjuchum.scraper.openapi.token;

import com.ant.juchumjuchum.common.http.CustomHttpConnection;
import com.ant.juchumjuchum.config.StockAccountProperties;
import com.ant.juchumjuchum.scraper.openapi.token.domain.OpenApiAccountInfo;
import com.ant.juchumjuchum.scraper.openapi.token.domain.OpenApiToken;
import com.ant.juchumjuchum.scraper.openapi.token.dto.TokenResponse;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import lombok.Getter;
import org.springframework.stereotype.Service;

@Getter
@Service
public class OpenApiTokenService {

    private final StockAccountProperties stockAccountProperties;
    private final CustomHttpConnection customHttpConnection;
    private List<OpenApiAccountInfo> openApiAccountInfos;

    public OpenApiTokenService(StockAccountProperties stockAccountProperties,
                               CustomHttpConnection customHttpConnection) {
        this.stockAccountProperties = stockAccountProperties;
        this.customHttpConnection = customHttpConnection;
        if (stockAccountProperties.getAccounts().isEmpty()) {
            throw new IllegalArgumentException("The number of accounts must be at least one.");
        }

        if (stockAccountProperties.getAccounts().size() != stockAccountProperties.getPasswords().size() ||
                stockAccountProperties.getAccounts().size() != stockAccountProperties.getKeys().size()) {
            throw new IllegalArgumentException("The number of accounts, passwords, and keys must be the same.");
        }
        try {
            init(stockAccountProperties);
        } catch (IOException | URISyntaxException | InterruptedException e) {
            throw new IllegalArgumentException("Failed to initialize OpenApiAccountInfo", e);
        }

    }

    private void init(StockAccountProperties stockAccountProperties)
            throws IOException, URISyntaxException, InterruptedException {
        openApiAccountInfos = new ArrayList<>();
        for (int i = 0; i < stockAccountProperties.getAccounts().size(); i++) {
            Long account = stockAccountProperties.getAccounts().get(i);
            String password = stockAccountProperties.getPasswords().get(i);
            String key = stockAccountProperties.getKeys().get(i);
            Map<String, String> body = Map.of(
                    "grant_type", "client_credentials",
                    "appsecret", password,
                    "appkey", key
            );
            TokenResponse result = (TokenResponse) customHttpConnection.postRequest(
                    stockAccountProperties.getOpenApiUrl() + "/oauth2/tokenP",
                    body, TokenResponse.class);
            OpenApiToken openApiToken = OpenApiToken.builder()
                    .account(account)
                    .build();
            openApiToken.updateToken(result);
            OpenApiAccountInfo openApiAccountInfo = OpenApiAccountInfo.builder()
                    .account(account)
                    .password(password)
                    .key(key)
                    .openApiToken(openApiToken)
                    .build();
            openApiAccountInfos.add(openApiAccountInfo);
        }
    }
}
