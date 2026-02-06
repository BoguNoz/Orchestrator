package com.bogunoz.projects.orchestrator.fundry.config;

import lombok.Getter;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Getter
@ConfigurationProperties(prefix = "openai.client")
public class OpenAIClientProperties {
    private String apiKey;
    private String endpoint;
}
