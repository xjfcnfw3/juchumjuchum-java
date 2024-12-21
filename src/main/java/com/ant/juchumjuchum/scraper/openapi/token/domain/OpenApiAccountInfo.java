package com.ant.juchumjuchum.scraper.openapi.token.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;


@Getter
@Builder
@EqualsAndHashCode
@AllArgsConstructor
public class OpenApiAccountInfo {
    private Long account;
    private String password;
    private String key;
    private OpenApiToken openApiToken;
}
