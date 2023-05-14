package com.dannyhromau.watcher.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.time.ZonedDateTime;
import java.util.List;

@Getter
@Setter
@Entity
@Table(name = "coin")
@AllArgsConstructor
@NoArgsConstructor
public class Coin {

    @Id
    @Column(name = "id", unique = true)
    private int id;
    @Column(name = "symbol", unique = true)
    private String symbol;
    @Column(name = "name")
    private String name;
    @Column(name = "price_usd")
    private double priceUsd;
    @Column(name = "updated_on")
    private ZonedDateTime updatedOn;
    @OneToMany(mappedBy = "coin")
    private List<NotificationEntity> notifications;

}
