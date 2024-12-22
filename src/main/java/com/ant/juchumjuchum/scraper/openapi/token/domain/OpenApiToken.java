package com.ant.juchumjuchum.scraper.openapi.token.domain;

import com.ant.juchumjuchum.scraper.openapi.token.dto.TokenResponse;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Builder
@Getter
@Setter
@Entity(name = "open_api_token")
@AllArgsConstructor
@NoArgsConstructor
public class OpenApiToken {

    @Id
    private Long account;

    private String token;

    @Column(columnDefinition = "TIMESTAMP", name = "tokenExpire")
    private LocalDateTime tokenExpireAt;

    private String websocketKey;

    @Column(columnDefinition = "TIMESTAMP", name = "websocketKeyExpire")
    private LocalDateTime websocketKeyExpireAt;

    public void updateToken(TokenResponse tokenResponse) {
        this.token = tokenResponse.getAccessToken();
        this.tokenExpireAt = tokenResponse.getAccessTokenTokenExpired();
    }
}
