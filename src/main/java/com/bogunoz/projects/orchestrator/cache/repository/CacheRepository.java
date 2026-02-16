package com.bogunoz.projects.orchestrator.cache.repository;

import com.bogunoz.projects.orchestrator.contract.websocket.model.ChatMessage;

import java.util.List;
import java.util.concurrent.CompletableFuture;

public interface CacheRepository<T> {
    CompletableFuture<Void> addAsync(T object, String key);
    CompletableFuture<List<T>> getAllAsync(String key);
}
