package com.dannyhromau.watcher.repository;

import com.dannyhromau.watcher.model.Coin;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CoinRepository extends JpaRepository<Coin, Integer> {

    Coin findCoinBySymbol(String symbol);
}
