package com.bogunoz.projects.orchestrator.search.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Setter
@Getter
@ConfigurationProperties(prefix = "search.api")
public class SearchApiProperties {
    private String key;
    private String endpoint;
}
