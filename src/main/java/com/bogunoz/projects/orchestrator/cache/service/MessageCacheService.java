package com.bogunoz.projects.orchestrator.cache.service;

import com.azure.core.annotation.ServiceInterface;
import com.bogunoz.projects.orchestrator.contract.websocket.model.ChatMessage;
import org.springframework.modulith.NamedInterface;

import java.util.List;
import java.util.concurrent.CompletableFuture;

@NamedInterface
@ServiceInterface(name = "cache-service")
public interface MessageCacheService {
    CompletableFuture<Void> addMessage(ChatMessage message);
    CompletableFuture<List<ChatMessage>> getAllMessages();
}
