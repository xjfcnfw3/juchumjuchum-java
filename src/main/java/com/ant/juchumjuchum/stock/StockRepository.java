package com.ant.juchumjuchum.stock;

import com.ant.juchumjuchum.stock.domain.Stock;
import org.springframework.data.jpa.repository.JpaRepository;

public interface StockRepository extends JpaRepository<Stock, String> {
}
