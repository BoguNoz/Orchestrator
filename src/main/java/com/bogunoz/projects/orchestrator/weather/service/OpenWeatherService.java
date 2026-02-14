package com.bogunoz.projects.orchestrator.weather.service;

import com.bogunoz.projects.orchestrator.common.constant.Error;
import com.bogunoz.projects.orchestrator.common.model.Response;
import com.bogunoz.projects.orchestrator.contract.weather.WeatherForecastRequest;
import com.bogunoz.projects.orchestrator.contract.weather.WeatherForecastResponse;
import com.bogunoz.projects.orchestrator.weather.client.WeatherClient;
import com.bogunoz.projects.orchestrator.weather.model.LocationModel;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.jspecify.annotations.Nullable;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.concurrent.CompletableFuture;

@Service
public class OpenWeatherService implements WeatherService {

    // region IoC
    private final WeatherClient weatherApiClient;
    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();
    // endregion IoC

    public OpenWeatherService(WeatherClient weatherApiClient) {
        this.weatherApiClient = weatherApiClient;
    }

    @Override
    public CompletableFuture<Response<WeatherForecastResponse>> getWeatherForecastAsync(
            WeatherForecastRequest request) {

        return resolveLocation(request)
                .thenCompose(location -> weatherApiClient
                        .getCurrentWeatherAsync(location.getLat(), location.getLon())
                        .thenApply(response -> mapToWeatherForecastSafe(response, request.getCityName()))
                )
                .handle((response, ex) -> {
                    if (response == null || ex != null) {
                        return Response.error(Error.EXTERNAL_SERVICE_ERROR);
                    }
                    return Response.ok(response);
                });
    }

    private CompletableFuture<LocationModel> resolveLocation(WeatherForecastRequest request) {

        if (!request.isUseCityName()) {
            LocationModel location = new LocationModel(request.getLatitude(), request.getLatitude());
            return CompletableFuture.completedFuture(location);
        }

        if (!StringUtils.hasLength(request.getCityName())) {
            return CompletableFuture.failedFuture(
                    new IllegalArgumentException(Error.INCORRECT_SERVICE_REQUEST)
            );
        }

        return weatherApiClient.getGeocodingAsync(request.getCityName())
                .thenApply(OpenWeatherService::mapToLocationModel)
                .thenCompose(location -> {
                    if (location == null) {
                        return CompletableFuture.failedFuture(new RuntimeException(Error.EXTERNAL_SERVICE_ERROR));
                    }
                    return CompletableFuture.completedFuture(location);
                });

    }

    private static LocationModel mapToLocationModel(String json) {
        try {
            List<LocationModel> locations = OBJECT_MAPPER.readValue(json, new TypeReference<>() {});
            return locations.isEmpty() ? null : locations.get(0);
        } catch (JsonProcessingException e) {
            return null;
        }
    }

    private static @Nullable WeatherForecastResponse mapToWeatherForecastSafe(String json, String location) {
        try {
            JsonNode root = OBJECT_MAPPER.readTree(json);

            JsonNode main = root.path("main");
            JsonNode weatherArray = root.path("weather");

            String description = "";
            if (weatherArray.isArray() && !weatherArray.isEmpty()) {
                description = weatherArray.get(0).path("description").asText();
            }

            WeatherForecastResponse response = new WeatherForecastResponse();
            response.setWeatherDescription(description);
            response.setTemperature((float) main.path("temp").asDouble());
            response.setFeelsLike((float) main.path("feels_like").asDouble());
            response.setTempMin((float) main.path("temp_min").asDouble());
            response.setTempMax((float) main.path("temp_max").asDouble());
            response.setPressure(main.path("pressure").asInt());
            response.setHumidity(main.path("humidity").asInt());
            response.setLocation(location);

            return response;

        } catch (Exception e) {
            return null;
        }
    }
}
