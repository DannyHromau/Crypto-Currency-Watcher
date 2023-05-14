package com.dannyhromau.watcher.api.external;

import lombok.*;

import java.time.ZonedDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ExternalCoinDto {
    private int id;

    private String symbol;

    private String name;

    private double priceUsd;

    private ZonedDateTime updatedOn;


}
