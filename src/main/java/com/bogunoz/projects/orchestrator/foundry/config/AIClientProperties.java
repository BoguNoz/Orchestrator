package com.bogunoz.projects.orchestrator.foundry.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Setter
@Getter
@ConfigurationProperties(prefix = "ai.client")
public class AIClientProperties {
    private String key;
    private String endpoint;
    private String model;
    private String deploymentName;
    private int maxTokens;
    private double temperature;
    private double topP;
    private String buildPromptContext;
}
