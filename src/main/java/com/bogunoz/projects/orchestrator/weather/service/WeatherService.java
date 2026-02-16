package com.bogunoz.projects.orchestrator.weather.service;

import com.azure.core.annotation.ServiceInterface;
import com.bogunoz.projects.orchestrator.common.model.Response;
import com.bogunoz.projects.orchestrator.contract.weather.WeatherForecastRequest;
import com.bogunoz.projects.orchestrator.contract.weather.WeatherForecastResponse;
import org.springframework.modulith.NamedInterface;

import java.io.IOException;
import java.util.concurrent.CompletableFuture;

@NamedInterface
@ServiceInterface(name = "weather")
public interface WeatherService {
    CompletableFuture<Response<WeatherForecastResponse>> getWeatherForecastAsync(WeatherForecastRequest request) throws IOException, InterruptedException;
}
