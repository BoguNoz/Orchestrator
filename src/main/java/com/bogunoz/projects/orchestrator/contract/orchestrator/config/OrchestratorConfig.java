package com.bogunoz.projects.orchestrator.contract.orchestrator.config;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableConfigurationProperties(OrchestratorProperties.class)
public class OrchestratorConfig {
}
