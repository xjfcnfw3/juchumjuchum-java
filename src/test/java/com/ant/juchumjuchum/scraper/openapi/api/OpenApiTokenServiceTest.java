package com.ant.juchumjuchum.scraper.openapi.api;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.ant.juchumjuchum.common.http.CustomHttpConnection;
import com.ant.juchumjuchum.config.StockAccount;
import com.ant.juchumjuchum.config.StockAccountProperties;
import com.ant.juchumjuchum.scraper.openapi.token.OpenApiTokenService;
import com.ant.juchumjuchum.scraper.openapi.token.TokenApiTokenRepository;
import com.ant.juchumjuchum.scraper.openapi.token.domain.OpenApiAccountInfo;
import com.ant.juchumjuchum.scraper.openapi.token.domain.OpenApiToken;
import com.ant.juchumjuchum.scraper.openapi.token.dto.TokenResponse;
import java.io.IOException;
import java.net.URISyntaxException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class OpenApiTokenServiceTest {

    private final TokenResponse tokenResponse = TokenResponse.builder()
            .accessToken("token")
            .tokenType("type")
            .accessTokenTokenExpired(LocalDateTime.of(2021, 1, 1, 0, 0, 0))
            .expireAt(1000)
            .build();
    private final OpenApiToken openApiToken = OpenApiToken.builder()
            .account(1L)
            .token("token")
            .tokenExpireAt(LocalDateTime.of(2021, 1, 1, 0, 0, 0))
            .build();

    @Mock
    private StockAccountProperties stockAccountProperties;
    @Mock
    private CustomHttpConnection customHttpConnection;
    @Mock
    private TokenApiTokenRepository tokenRepository;

    @Test
    @DisplayName("계좌 정보가 비어있을 때 예외 발생")
    void emptyAccounts() {
        when(stockAccountProperties.getAccounts()).thenReturn(new ArrayList<>());

        assertThrows(IllegalArgumentException.class,
                () -> new OpenApiTokenService(stockAccountProperties, customHttpConnection, tokenRepository));
    }

    @Test
    @DisplayName("계좌 정보 초기화")
    void initAccount() throws IOException, URISyntaxException, InterruptedException {
        StockAccount stockAccount1 = new StockAccount(1L, "a", "b");
        StockAccount stockAccount2 = new StockAccount(2L, "b", "e");
        StockAccount stockAccount3 = new StockAccount(3L, "c", "f");
        List<StockAccount> accounts = List.of(stockAccount1, stockAccount2, stockAccount3);
        when(stockAccountProperties.getAccounts()).thenReturn(accounts);
        when(customHttpConnection.postRequest(anyString(), any(), any()))
                .thenReturn(tokenResponse);
        OpenApiTokenService openApiTokenService = new OpenApiTokenService(stockAccountProperties, customHttpConnection,
                tokenRepository);

        List<OpenApiAccountInfo> accountInfos = openApiTokenService.getOpenApiAccountInfos();
        List<StockAccount> stockAccounts = accountInfos.stream().map(OpenApiAccountInfo::getStockAccount).toList();

        assertThat(stockAccounts).isEqualTo(accounts);
    }

    @Test
    @DisplayName("계좌 토큰 요청")
    void initAccountToken() throws IOException, URISyntaxException, InterruptedException {
        StockAccount stockAccount = new StockAccount(1L, "a", "b");
        when(stockAccountProperties.getAccounts()).thenReturn(List.of(stockAccount));
        when(customHttpConnection.postRequest(anyString(), any(), any()))
                .thenReturn(tokenResponse);
        OpenApiTokenService openApiTokenService = new OpenApiTokenService(stockAccountProperties, customHttpConnection,
                tokenRepository);

        List<OpenApiAccountInfo> openApiAccountInfos = openApiTokenService.getOpenApiAccountInfos();
        OpenApiToken result = openApiAccountInfos.get(0).getOpenApiToken();

        assertThat(result.getAccount()).isEqualTo(openApiToken.getAccount());
        assertThat(result.getToken()).isEqualTo(openApiToken.getToken());
        assertThat(result.getTokenExpireAt()).isEqualTo(openApiToken.getTokenExpireAt());
    }

    @Test
    @DisplayName("계좌 정보가 비어있을 때 토큰 요청")
    void initBlankAccountToken() throws IOException, URISyntaxException, InterruptedException {
        StockAccount stockAccount = new StockAccount(1L, "a", "b");
        when(stockAccountProperties.getAccounts()).thenReturn(List.of(stockAccount));
        when(customHttpConnection.postRequest(anyString(), any(), any()))
                .thenReturn(tokenResponse);
        when(tokenRepository.findById(any())).thenReturn(Optional.empty());

        new OpenApiTokenService(stockAccountProperties, customHttpConnection, tokenRepository);

        verify(customHttpConnection, atLeastOnce()).postRequest(any(), any(), any());
    }

    @Test
    @DisplayName("계좌 정보가 만료되었을 때 토큰 요청")
    void initExpiredAccountToken() throws IOException, URISyntaxException, InterruptedException {
        StockAccount stockAccount = new StockAccount(1L, "a", "b");
        when(stockAccountProperties.getAccounts()).thenReturn(List.of(stockAccount));
        OpenApiToken mockToken = mock(OpenApiToken.class);
        when(mockToken.isExpiredToken()).thenReturn(true);
        when(tokenRepository.findById(any())).thenReturn(Optional.of(mockToken));

        new OpenApiTokenService(stockAccountProperties, customHttpConnection, tokenRepository);

        verify(customHttpConnection, atLeastOnce()).postRequest(any(), any(), any());
        verify(tokenRepository, atLeastOnce()).save(any());
    }

    @Test
    @DisplayName("이미 정보가 존재하면 요청하지 않음")
    void initExistAccountToken() throws IOException, URISyntaxException, InterruptedException {
        StockAccount stockAccount = new StockAccount(1L, "a", "b");
        when(stockAccountProperties.getAccounts()).thenReturn(List.of(stockAccount));
        OpenApiToken mockToken = mock(OpenApiToken.class);
        when(mockToken.isExpiredToken()).thenReturn(false);
        when(tokenRepository.findById(any())).thenReturn(Optional.of(mockToken));

        new OpenApiTokenService(stockAccountProperties, customHttpConnection, tokenRepository);

        verify(customHttpConnection, never()).postRequest(any(), any(), any());
    }
}
