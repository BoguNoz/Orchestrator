package com.bogunoz.projects.orchestrator.foundry.service;

import com.azure.ai.openai.models.ChatResponseMessage;
import com.azure.core.annotation.ServiceInterface;
import com.bogunoz.projects.orchestrator.common.model.Response;
import com.bogunoz.projects.orchestrator.contract.foundry.FoundryChatRequest;
import org.springframework.modulith.NamedInterface;

import java.util.concurrent.CompletableFuture;


@NamedInterface
@ServiceInterface(name = "foundry")
public interface AIService {
    CompletableFuture<Response<ChatResponseMessage>> askChatAsync(FoundryChatRequest request);
}
