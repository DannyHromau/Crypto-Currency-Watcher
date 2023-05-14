package com.dannyhromau.watcher.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import java.io.Serializable;

@Getter
@Setter
@Embeddable
@AllArgsConstructor
@NoArgsConstructor
public class NotificationEntityKey implements Serializable {

    @Column(name = "username")
    private String username;
    @Column(name = "coin_id")
    private int coinId;

}
