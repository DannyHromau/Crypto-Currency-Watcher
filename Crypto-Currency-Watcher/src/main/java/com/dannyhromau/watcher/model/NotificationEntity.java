package com.dannyhromau.watcher.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.time.ZonedDateTime;

@Getter
@Setter
@Entity
@Table(name = "notification")
@AllArgsConstructor
@NoArgsConstructor
public class NotificationEntity {
    @EmbeddedId
    private NotificationEntityKey key;

    @Column(name = "username", insertable = false, updatable = false)
    private String username;
    @Column(name = "coin_id", insertable = false, updatable = false)
    private int coinId;
    @Column(name = "price_usd")
    private double priceUsd;
    @Column(name = "created_on")
    private ZonedDateTime createdOn;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "coin_id", insertable = false, updatable = false)
    private Coin coin;


}
