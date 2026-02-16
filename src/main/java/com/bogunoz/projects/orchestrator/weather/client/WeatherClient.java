package com.bogunoz.projects.orchestrator.weather.client;

import com.azure.core.annotation.ServiceInterface;

import java.util.concurrent.CompletableFuture;

@ServiceInterface(name = "weather-client")
public interface WeatherClient {
    CompletableFuture<String> getGeocodingAsync(String city);
    CompletableFuture<String> getCurrentWeatherAsync(double lat, double lon);
}
