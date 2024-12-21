package com.ant.juchumjuchum.scraper.openapi.token.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import java.time.LocalDateTime;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity(name = "open_api_token")
public class OpenApiToken {

    @Id
    private Long account;

    private String token;

    @Column(columnDefinition = "TIMESTAMP", name = "tokenExpire")
    private LocalDateTime tokenExpireAt;

    private String websocketKey;

    @Column(columnDefinition = "TIMESTAMP", name = "websocketKeyExpire")
    private LocalDateTime websocketKeyExpireAt;
}
