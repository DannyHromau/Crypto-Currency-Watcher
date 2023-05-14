package com.dannyhromau.watcher.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.List;

@Getter
@Setter
@ConfigurationProperties(prefix = "coins")
public class ExternalApiConfig {
    private List<String> ids;
}
