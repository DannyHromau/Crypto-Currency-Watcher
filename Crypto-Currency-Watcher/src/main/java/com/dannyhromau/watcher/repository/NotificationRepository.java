package com.dannyhromau.watcher.repository;

import com.dannyhromau.watcher.model.NotificationEntity;
import com.dannyhromau.watcher.model.NotificationEntityKey;
import org.springframework.data.jpa.repository.JpaRepository;

public interface NotificationRepository extends JpaRepository<NotificationEntity, NotificationEntityKey> {


}
