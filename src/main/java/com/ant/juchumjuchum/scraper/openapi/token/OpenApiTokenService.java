package com.ant.juchumjuchum.scraper.openapi.token;

import com.ant.juchumjuchum.common.http.CustomHttpConnection;
import com.ant.juchumjuchum.config.StockAccount;
import com.ant.juchumjuchum.config.StockAccountProperties;
import com.ant.juchumjuchum.scraper.openapi.token.domain.OpenApiAccountInfo;
import com.ant.juchumjuchum.scraper.openapi.token.domain.OpenApiToken;
import com.ant.juchumjuchum.scraper.openapi.token.dto.TokenResponse;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import lombok.Getter;
import org.springframework.stereotype.Service;

@Getter
@Service
public class OpenApiTokenService {
    private final CustomHttpConnection customHttpConnection;
    private final TokenApiTokenRepository tokenRepository;

    private List<OpenApiAccountInfo> openApiAccountInfos;

    public OpenApiTokenService(StockAccountProperties stockAccountProperties,
                               CustomHttpConnection customHttpConnection,
                               TokenApiTokenRepository tokenRepository) {
        this.tokenRepository = tokenRepository;
        this.customHttpConnection = customHttpConnection;
        List<StockAccount> accounts = stockAccountProperties.getAccounts();

        if (accounts.isEmpty()) {
            throw new IllegalArgumentException("The number of accounts must be at least one.");
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
        for (StockAccount stockAccount : stockAccountProperties.getAccounts()) {
            Map<String, String> body = Map.of(
                    "grant_type", "client_credentials",
                    "appsecret", stockAccount.getPassword(),
                    "appkey", stockAccount.getKey()
            );
            Optional<OpenApiToken> token = tokenRepository.findById(stockAccount.getAccount());
            if (token.isPresent()) {
                OpenApiToken openApiToken = token.get();

                if (openApiToken.isExpiredToken()) {
                    TokenResponse result = (TokenResponse) customHttpConnection.postRequest(
                            stockAccountProperties.getOpenApiUrl() + "/oauth2/tokenP",
                            body, TokenResponse.class);
                    openApiToken.updateToken(result);
                    tokenRepository.save(openApiToken);
                }

                OpenApiAccountInfo openApiAccountInfo = OpenApiAccountInfo.builder()
                        .stockAccount(stockAccount)
                        .openApiToken(openApiToken)
                        .build();
                openApiAccountInfos.add(openApiAccountInfo);
            } else {
                OpenApiToken openApiToken = OpenApiToken.builder()
                        .account(stockAccount.getAccount())
                        .build();
                TokenResponse result = (TokenResponse) customHttpConnection.postRequest(
                        stockAccountProperties.getOpenApiUrl() + "/oauth2/tokenP",
                        body, TokenResponse.class);
                openApiToken.updateToken(result);
                tokenRepository.save(openApiToken);
                OpenApiAccountInfo openApiAccountInfo = OpenApiAccountInfo.builder()
                        .stockAccount(stockAccount)
                        .openApiToken(openApiToken)
                        .build();
                openApiAccountInfos.add(openApiAccountInfo);
            }
        }
    }
}
