package com.ant.juchumjuchum.scraper.openapi.api;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.ant.juchumjuchum.config.StockAccountProperties;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class OpenApiTokenServiceTest {

    @Test
    @DisplayName("계좌 정보가 비어있을 때 예외 발생")
    void emptyAccounts() {
        StockAccountProperties stockAccountProperties = mock(StockAccountProperties.class);
        when(stockAccountProperties.getAccounts()).thenReturn(new ArrayList<>());
        assertThrows(IllegalArgumentException.class, () -> new OpenApiTokenService(stockAccountProperties)
        );
    }

    @Test
    @DisplayName("계좌 개수와 비밀번호 개수가 다를 때 예외 발생")
    void differentSizeAccountAndPassword() {
        StockAccountProperties stockAccountProperties = mock(StockAccountProperties.class);
        when(stockAccountProperties.getAccounts()).thenReturn(List.of(1L, 2L, 3L));
        when(stockAccountProperties.getPasswords()).thenReturn(List.of("a", "b"));
        assertThrows(IllegalArgumentException.class, () -> new OpenApiTokenService(stockAccountProperties)
        );
    }

    @Test
    @DisplayName("계좌 개수와 비밀번호 개수가 다를 때 예외 발생")
    void differentSizePasswordAndKeys() {
        StockAccountProperties stockAccountProperties = mock(StockAccountProperties.class);
        when(stockAccountProperties.getAccounts()).thenReturn(List.of(1L, 2L, 3L));
        when(stockAccountProperties.getPasswords()).thenReturn(List.of("a", "b", "c"));
        when(stockAccountProperties.getKeys()).thenReturn(List.of("d", "e"));
        assertThrows(IllegalArgumentException.class, () -> new OpenApiTokenService(stockAccountProperties)
        );
    }
}
