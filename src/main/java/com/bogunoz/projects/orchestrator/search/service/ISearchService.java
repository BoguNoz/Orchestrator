package com.bogunoz.projects.orchestrator.search.service;

import com.azure.core.annotation.ServiceInterface;
import com.bogunoz.projects.orchestrator.common.model.Response;
import org.springframework.modulith.NamedInterface;

import java.util.List;
import java.util.concurrent.CompletableFuture;

@NamedInterface
@ServiceInterface(name = "search")
public interface ISearchService {
    CompletableFuture<Response<String>> searchForAsync(List<String> searchKeys);
}
