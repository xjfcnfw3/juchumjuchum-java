package com.ant.juchumjuchum.scraper.openapi.api;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

import com.ant.juchumjuchum.common.http.CustomHttpConnection;
import com.ant.juchumjuchum.config.StockAccountProperties;
import com.ant.juchumjuchum.scraper.openapi.token.OpenApiTokenService;
import com.ant.juchumjuchum.scraper.openapi.token.domain.OpenApiAccountInfo;
import com.ant.juchumjuchum.scraper.openapi.token.domain.OpenApiToken;
import com.ant.juchumjuchum.scraper.openapi.token.dto.TokenResponse;
import java.io.IOException;
import java.net.URISyntaxException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
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
    @Mock
    private StockAccountProperties stockAccountProperties;
    @Mock
    private CustomHttpConnection customHttpConnection;

    @Test
    @DisplayName("계좌 정보가 비어있을 때 예외 발생")
    void emptyAccounts() {
        when(stockAccountProperties.getAccounts()).thenReturn(new ArrayList<>());

        assertThrows(IllegalArgumentException.class,
                () -> new OpenApiTokenService(stockAccountProperties, customHttpConnection));
    }

    @Test
    @DisplayName("계좌 개수와 비밀번호 개수가 다를 때 예외 발생")
    void differentSizeAccountAndPassword() {
        when(stockAccountProperties.getAccounts()).thenReturn(List.of(1L, 2L, 3L));
        when(stockAccountProperties.getPasswords()).thenReturn(List.of("a", "b"));

        assertThrows(IllegalArgumentException.class,
                () -> new OpenApiTokenService(stockAccountProperties, customHttpConnection));
    }

    @Test
    @DisplayName("계좌 개수와 비밀번호 개수가 다를 때 예외 발생")
    void differentSizePasswordAndKeys() {
        when(stockAccountProperties.getAccounts()).thenReturn(List.of(1L, 2L, 3L));
        when(stockAccountProperties.getPasswords()).thenReturn(List.of("a", "b", "c"));
        when(stockAccountProperties.getKeys()).thenReturn(List.of("d", "e"));

        assertThrows(IllegalArgumentException.class,
                () -> new OpenApiTokenService(stockAccountProperties, customHttpConnection));
    }

    @Test
    @DisplayName("계좌 정보 초기화")
    void initAccount() throws IOException, URISyntaxException, InterruptedException {
        when(stockAccountProperties.getAccounts()).thenReturn(List.of(1L, 2L, 3L));
        when(stockAccountProperties.getPasswords()).thenReturn(List.of("a", "b", "c"));
        when(stockAccountProperties.getKeys()).thenReturn(List.of("d", "e", "f"));
        when(customHttpConnection.postRequest(anyString(), any(), any()))
                .thenReturn(tokenResponse);
        OpenApiTokenService openApiTokenService = new OpenApiTokenService(stockAccountProperties, customHttpConnection);

        List<OpenApiAccountInfo> accountInfos = openApiTokenService.getOpenApiAccountInfos();

        for (int i = 0; i < accountInfos.size(); i++) {
            assertThat(accountInfos.get(i).getAccount()).isEqualTo(i + 1);
            assertThat(accountInfos.get(i).getPassword()).isEqualTo(String.valueOf((char) ('a' + i)));
            assertThat(accountInfos.get(i).getKey()).isEqualTo(String.valueOf((char) ('d' + i)));
        }
    }

    @Test
    @DisplayName("계좌 토큰 요청")
    void initAccountToken() throws IOException, URISyntaxException, InterruptedException {
        OpenApiToken openApiToken = OpenApiToken.builder()
                .account(1L)
                .token("token")
                .tokenExpireAt(LocalDateTime.of(2021, 1, 1, 0, 0, 0))
                .build();
        when(stockAccountProperties.getAccounts()).thenReturn(List.of(1L));
        when(stockAccountProperties.getPasswords()).thenReturn(List.of("a"));
        when(stockAccountProperties.getKeys()).thenReturn(List.of("b"));
        when(customHttpConnection.postRequest(anyString(), any(), any()))
                .thenReturn(tokenResponse);
        OpenApiTokenService openApiTokenService = new OpenApiTokenService(stockAccountProperties, customHttpConnection);

        List<OpenApiAccountInfo> openApiAccountInfos = openApiTokenService.getOpenApiAccountInfos();
        OpenApiToken result = openApiAccountInfos.get(0).getOpenApiToken();

        assertThat(result.getAccount()).isEqualTo(openApiToken.getAccount());
        assertThat(result.getToken()).isEqualTo(openApiToken.getToken());
        assertThat(result.getTokenExpireAt()).isEqualTo(openApiToken.getTokenExpireAt());
    }
}
