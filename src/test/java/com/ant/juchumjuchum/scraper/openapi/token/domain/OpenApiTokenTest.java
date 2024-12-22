package com.ant.juchumjuchum.scraper.openapi.token.domain;

import static org.assertj.core.api.Assertions.assertThat;

import com.ant.juchumjuchum.scraper.openapi.token.dto.TokenResponse;
import java.time.LocalDateTime;
import java.util.UUID;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class OpenApiTokenTest {

    @Test
    @DisplayName("토큰 업데이트")
    void updateToken() {
        String accessToken = UUID.randomUUID().toString();
        LocalDateTime tokenExpired = LocalDateTime.of(2021, 8, 1, 0, 0);
        OpenApiToken openApiToken = new OpenApiToken();
        TokenResponse tokenResponse = TokenResponse.builder()
                .accessToken(accessToken)
                .accessTokenTokenExpired(tokenExpired)
                .build();

        openApiToken.updateToken(tokenResponse);

        assertThat(openApiToken.getToken()).isEqualTo(accessToken);
        assertThat(openApiToken.getTokenExpireAt()).isEqualTo(tokenExpired);
    }
}
