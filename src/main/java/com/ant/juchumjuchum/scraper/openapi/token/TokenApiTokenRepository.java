package com.ant.juchumjuchum.scraper.openapi.token;

import com.ant.juchumjuchum.scraper.openapi.token.domain.OpenApiToken;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TokenApiTokenRepository extends JpaRepository<OpenApiToken, Long> {
}
