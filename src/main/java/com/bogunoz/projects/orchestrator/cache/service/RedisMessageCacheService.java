package com.bogunoz.projects.orchestrator.cache.service;

import com.bogunoz.projects.orchestrator.cache.repository.RedisRepository;
import com.bogunoz.projects.orchestrator.contract.websocket.model.ChatMessage;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.CompletableFuture;

@Service
public class RedisMessageCacheService implements MessageCacheService{

    // region IoC
    private final RedisRepository<ChatMessage> messageRepository;
    // endregion IoC

    public RedisMessageCacheService(RedisRepository<ChatMessage> messageRepository) {
        this.messageRepository = messageRepository;
    }

    @Override
    public CompletableFuture<Void> addMessage(ChatMessage message) {
        return messageRepository.addAsync(message, message.getSessionId());
    }

    @Override
    public CompletableFuture<List<ChatMessage>> getAllMessages(String sessionId) {
        return messageRepository.getAllAsync(sessionId);
    }
}
