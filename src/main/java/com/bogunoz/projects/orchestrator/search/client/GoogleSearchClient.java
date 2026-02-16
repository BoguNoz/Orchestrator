package com.bogunoz.projects.orchestrator.search.client;

import com.bogunoz.projects.orchestrator.search.config.SearchApiProperties;
import org.springframework.stereotype.Component;

import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.CompletableFuture;

@Component
public class GoogleSearchClient implements SearchClient {
    // region IoC
    private final HttpClient client;
    private final SearchApiProperties props;
    // endregion IoC


    public GoogleSearchClient(HttpClient client, SearchApiProperties props) {
        this.client = client;
        this.props = props;
    }

    @Override
    public CompletableFuture<String> getSearchResult(String query) {
        var url = String.format(
                "%s?key=%s&cx=%s&q=%s",
                props.getEndpoint(),
                props.getKey(),
                props.getCx(),
                URLEncoder.encode(query, StandardCharsets.UTF_8)
        );

        var request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .GET()
                .build();

        return client.sendAsync(request, HttpResponse.BodyHandlers.ofString())
                .thenApply(HttpResponse::body);
    }
}
