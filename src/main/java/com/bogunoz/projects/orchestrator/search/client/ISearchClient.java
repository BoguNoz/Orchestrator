package com.bogunoz.projects.orchestrator.search.client;

import com.azure.core.annotation.ServiceInterface;

import java.util.concurrent.CompletableFuture;

@ServiceInterface(name = "search-client")
public interface ISearchClient {

    CompletableFuture<String> getSearchResult(String query);
}
