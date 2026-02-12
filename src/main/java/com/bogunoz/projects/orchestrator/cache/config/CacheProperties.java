package com.bogunoz.projects.orchestrator.cache.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Setter
@Getter
@ConfigurationProperties(prefix = "cache")
public class CacheProperties {
    private String host;
    private String port;
    private String prefix;
}
