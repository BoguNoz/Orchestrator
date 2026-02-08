package com.bogunoz.projects.orchestrator.contract.weather;

import org.springframework.modulith.NamedInterface;

@NamedInterface
public record WeatherForecastRequest (
     double latitude,
     double longitude,
     String cityName,
     boolean useCityName
) {}
