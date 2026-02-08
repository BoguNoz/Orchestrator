package com.bogunoz.projects.orchestrator.weather.client;

import com.azure.core.annotation.ServiceInterface;

import java.io.IOException;
import java.util.concurrent.CompletableFuture;

@ServiceInterface(name = "weather-client")
public interface IWeatherApiClient {
    CompletableFuture<String> getGeocodingAsync(String city);
    CompletableFuture<String> getCurrentWeatherAsync(double lat, double lon);
}
