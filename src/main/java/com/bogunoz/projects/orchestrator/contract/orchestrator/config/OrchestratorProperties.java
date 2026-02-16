package com.bogunoz.projects.orchestrator.contract.orchestrator.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.modulith.NamedInterface;

@Setter
@Getter
@NamedInterface
@ConfigurationProperties(prefix = "orchestrator")
public class OrchestratorProperties {
    private String toolWeather;
    private String toolSearch;
}
