package com.bogunoz.projects.orchestrator.cache.service;

import com.bogunoz.projects.orchestrator.cache.repository.RedisRepository;
import com.bogunoz.projects.orchestrator.contract.websocket.model.ChatMessage;

import java.util.List;
import java.util.concurrent.CompletableFuture;

public class RedisMessageCacheService implements MessageCacheService{

    // region IoC
    private final RedisRepository<ChatMessage> messageRepository;
    // endregion IoC

    public RedisMessageCacheService(RedisRepository<ChatMessage> messageRepository) {
        this.messageRepository = messageRepository;
    }

    @Override
    public CompletableFuture<Void> addMessage(ChatMessage message) {
        return messageRepository.addAsync(message, message.messageType().toString());
    }

    @Override
    public CompletableFuture<List<ChatMessage>> getAllMessages() {
        return messageRepository.getAllAsync();
    }
}
