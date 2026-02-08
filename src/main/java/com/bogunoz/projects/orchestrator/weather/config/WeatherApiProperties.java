package com.bogunoz.projects.orchestrator.weather.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Setter
@Getter
@ConfigurationProperties(prefix = "weather.api")
public class WeatherApiProperties {
    private String key;
    private String geocodingEndpoint;
    private String currentEndpoint;
}
