package com.dannyhromau.watcher.api.external;

import com.dannyhromau.watcher.exception.MethodNotAllowedException;

import java.util.Map;

public interface CoinApiProvider {
    Map<Integer, ExternalCoinDto> getExternalCoins() throws MethodNotAllowedException;
}
