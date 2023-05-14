package com.dannyhromau.watcher.service;

import com.dannyhromau.watcher.api.dto.request.NotificationDto;
import com.dannyhromau.watcher.api.dto.response.CoinDto;
import com.dannyhromau.watcher.api.dto.response.CoinPriceDto;
import com.dannyhromau.watcher.api.external.CoinApiProvider;
import com.dannyhromau.watcher.api.external.ExternalCoinDto;
import com.dannyhromau.watcher.config.VolatilityConfig;
import com.dannyhromau.watcher.exception.EmptyApiDataException;
import com.dannyhromau.watcher.exception.MethodNotAllowedException;
import com.dannyhromau.watcher.mapper.CoinMapper;
import com.dannyhromau.watcher.model.Coin;
import com.dannyhromau.watcher.model.NotificationEntity;
import com.dannyhromau.watcher.model.NotificationEntityKey;
import com.dannyhromau.watcher.repository.CoinRepository;
import com.dannyhromau.watcher.repository.NotificationRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.PostConstruct;
import javax.persistence.EntityNotFoundException;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Log4j2
@Service
@EnableAsync
@EnableScheduling
@Transactional
@RequiredArgsConstructor
public class WatcherService {

    private final CoinRepository coinRepository;
    private final CoinApiProvider coinApiProvider;
    private final NotificationRepository notificationRepository;

    private final VolatilityConfig volatilityConfig;


    @PostConstruct
    public void prepareCurrency(){
        try {
        Map<Integer, ExternalCoinDto> externalCoins = coinApiProvider.getExternalCoins();
        if (coinRepository.findAll().size() == 0) {
            coinRepository.saveAll(createCoins(externalCoins, CoinMapper.INSTANCE));
        }
        } catch (MethodNotAllowedException | EmptyApiDataException e) {
            log.warn(e.getMessage());
        }
    }


    @Scheduled(fixedRateString = "${update-interval}")
    @Async
    public void updateCurrency() {
        try {
            Map<Integer, ExternalCoinDto> externalCoins = coinApiProvider.getExternalCoins();
            List<Coin> coins = coinRepository.findAll();
            checkVolatility(coins, externalCoins, volatilityConfig.getVolatility());
            coins = coins.size() != 0 ?
                    updateCoins(externalCoins, coins, CoinMapper.INSTANCE) : createCoins(externalCoins, CoinMapper.INSTANCE);
            coinRepository.saveAll(coins);

        }catch (MethodNotAllowedException | EmptyApiDataException e) {
            log.warn(e.getMessage());
        }
    }


    public List<CoinDto> getCoinsList() {
        List<Coin> coins = coinRepository.findAll();
        List<CoinDto> coinDtoList = new ArrayList<>();
        if (coins.size() != 0) {
            for (Coin coin : coins) {
                coinDtoList.add(new CoinDto(coin.getId(), coin.getSymbol()));
            }
        } else {
            log.info("no coins in database");
            throw new EntityNotFoundException("list is empty");
        }

        return coinDtoList;
    }

    public CoinPriceDto getCoinBySymbol(String symbol) {
        Coin coin = coinRepository.findCoinBySymbol(symbol);
        if (coin == null) {
            log.info("coin doesn't exist with symbol {}, symbol", symbol);
            throw new EntityNotFoundException("entity not found");
        }
        return new CoinPriceDto(coin.getSymbol(), coin.getPriceUsd());
    }

    public NotificationDto createNotification(NotificationDto notificationDto) {
        Coin coin = coinRepository.findCoinBySymbol(notificationDto.getSymbol());
        if (coin == null) {
            log.info("coin doesn't exist with symbol {}, symbol", notificationDto.getSymbol());
            throw new EntityNotFoundException("entity not found");
        }
        NotificationEntityKey notificationEntityKey = new NotificationEntityKey(
                notificationDto.getUsername(), coin.getId());

        NotificationEntity notification = new NotificationEntity(
                notificationEntityKey,
                notificationDto.getUsername(),
                coin.getId(),
                coin.getPriceUsd(),
                ZonedDateTime.now(),
                coin
        );
        notificationRepository.save(notification);
        return notificationDto;
    }

    public static List<Coin> updateCoins(
            Map<Integer, ExternalCoinDto> externalCoinMap,
            List<Coin> coins,
            CoinMapper coinMapper) throws EmptyApiDataException {
        if (externalCoinMap.containsValue(null)) {
            throw new EmptyApiDataException("Empty data from external api");
        }
        for (Coin coin : coins) {
            coinMapper.updateCoinFromDto(externalCoinMap.get(coin.getId()), coin);
        }
        return coins;
    }

    public static List<Coin> createCoins(Map<Integer, ExternalCoinDto> externalCoinMap, CoinMapper coinMapper)
            throws EmptyApiDataException {
        if (externalCoinMap.containsValue(null)) {
            throw new EmptyApiDataException("Empty data from external api");
        }
        List<Coin> coins = new ArrayList<>();

        for (Map.Entry<Integer, ExternalCoinDto> entry : externalCoinMap.entrySet()) {
            ExternalCoinDto dto = entry.getValue();
            coins.add(coinMapper.toCoin(dto));
        }
        return coins;
    }


    public static void checkVolatility(List<Coin> coins,
                                       Map<Integer, ExternalCoinDto> externalCoinMap,
                                       double volatilityCoefficient) {

        if (coins.size() == 0) {
            log.info("no coins in database");
            throw new EntityNotFoundException("list is empty");
        }
        List<NotificationEntity> entities = new ArrayList<>();
        for (Coin coin : coins) {
            double oldPrice = coin.getPriceUsd();
            double newPrice = externalCoinMap.get(coin.getId()).getPriceUsd();
            double currentCoefficient = Math.abs(1 - oldPrice / newPrice);
            if (currentCoefficient >= volatilityCoefficient) {
                entities.addAll(coin.getNotifications());
            }
        }
        for (NotificationEntity entity : entities) {
            double percent = Math.abs(1 - (entity.getPriceUsd() / entity.getCoin().getPriceUsd())) * 100;
            log.warn(String.format("symbol : %s; username : %s; percent : %2f %%",
                    entity.getCoin().getSymbol(), entity.getUsername(), percent));
        }
    }
}
