package com.bogunoz.projects.orchestrator.search.service;

import com.bogunoz.projects.orchestrator.common.constant.Error;
import com.bogunoz.projects.orchestrator.common.model.Response;
import com.bogunoz.projects.orchestrator.search.client.ISearchClient;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

@Service
public class GoogleSearchService implements ISearchService{

    // region IoC
    private final ISearchClient searchClient;
    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();
    // endregion IoC

    public GoogleSearchService(ISearchClient searchClient) {
        this.searchClient = searchClient;
    }

    @Override
    public CompletableFuture<Response<String>> searchForAsync(List<String> searchKeys) {
        if (!searchKeys.isEmpty()) {
            return CompletableFuture.completedFuture(Response.ok(""));
        }

        var query = String.join(" ", searchKeys);
        return searchClient.getSearchResult(query)
            .thenApply(GoogleSearchService::getSnippet)
            .handle((response, ex) -> {
                if (response == null || ex != null) {
                    return Response.error(Error.EXTERNAL_SERVICE_ERROR);
                }
                return Response.ok(response);
            });
    }

    private static String getSnippet(String json) {
        try {
            JsonNode root = OBJECT_MAPPER.readTree(json);

            JsonNode items = root.get("items");
            return items
                .path(0)
                .path("snippet")
                .asText(null);

        } catch (Exception e) {
            return "";
        }
    }
}
