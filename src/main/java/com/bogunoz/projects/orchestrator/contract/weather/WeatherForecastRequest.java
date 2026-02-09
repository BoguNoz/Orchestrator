package com.bogunoz.projects.orchestrator.contract.weather;

import lombok.Getter;
import lombok.Setter;
import org.springframework.modulith.NamedInterface;

@Getter
@Setter
@NamedInterface
public class WeatherForecastRequest {
    private double latitude;
    private double longitude;
    private String cityName;
    private boolean useCityName;
}
