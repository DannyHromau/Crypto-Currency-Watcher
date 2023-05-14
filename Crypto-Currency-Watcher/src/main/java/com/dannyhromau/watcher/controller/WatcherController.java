package com.dannyhromau.watcher.controller;

import com.dannyhromau.watcher.api.dto.request.NotificationDto;
import com.dannyhromau.watcher.api.dto.response.CoinDto;
import com.dannyhromau.watcher.api.dto.response.CoinPriceDto;
import com.dannyhromau.watcher.service.WatcherService;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Log4j2
@RestController
@RequestMapping("/api/v1/watcher/coin")
@RequiredArgsConstructor
public class WatcherController {

    private final WatcherService watcherService;

    @GetMapping("/all")
    ResponseEntity<List<CoinDto>> getCoins(){
        log.info("call getCoins");
        return ResponseEntity.ok(watcherService.getCoinsList());
    }
    @GetMapping
    ResponseEntity<CoinPriceDto> getCoinByCode(@RequestParam(name = "code") String code){
        log.info("call getCoinByCode with code: {}, code", code);
        return ResponseEntity.ok(watcherService.getCoinBySymbol(code));
    }

    @PostMapping("/notify")
    ResponseEntity<NotificationDto> notifyCoin(@RequestBody NotificationDto notificationDto){
        log.info("call notifyCoin with data: {}, data", notificationDto);
        return ResponseEntity.ok(watcherService.createNotification(notificationDto));
    }


}
