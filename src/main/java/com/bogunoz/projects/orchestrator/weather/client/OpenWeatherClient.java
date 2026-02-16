package com.bogunoz.projects.orchestrator.weather.client;

import com.bogunoz.projects.orchestrator.weather.config.WeatherApiProperties;
import org.springframework.stereotype.Component;

import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.CompletableFuture;

@Component
public class OpenWeatherClient implements WeatherClient {

    // region IoC
    private final HttpClient client;
    private final WeatherApiProperties props;
    // endregion IoC

    public OpenWeatherClient(HttpClient client, WeatherApiProperties props) {
        this.client = client;
        this.props = props;
    }

    @Override
    public CompletableFuture<String> getGeocodingAsync(String city) {
        var url = String.format("%s?q=%s&limit=1&appid=%s",
                props.getGeocodingEndpoint(),
                URLEncoder.encode(city, StandardCharsets.UTF_8),
                props.getKey());

        var request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .GET()
                .build();

        return client.sendAsync(request, HttpResponse.BodyHandlers.ofString())
                .thenApply(HttpResponse::body);
    }

    @Override
    public CompletableFuture<String> getCurrentWeatherAsync(double lat, double lon) {
        var url = String.format("%s?lat=%s&lon=%s&units=metric&lang=pl&appid=%s",
                props.getCurrentEndpoint(),
                lat,
                lon,
                props.getKey());

        var request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .GET()
                .build();

        return client.sendAsync(request, HttpResponse.BodyHandlers.ofString())
                .thenApply(HttpResponse::body);
    }
}
