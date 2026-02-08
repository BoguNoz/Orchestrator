package com.bogunoz.projects.orchestrator.search.config;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;


@Configuration
@EnableConfigurationProperties(SearchApiProperties.class)
public class SearchApiConfig {
}
