package com.bogunoz.projects.orchestrator.orchestrator.handler;

import com.azure.ai.openai.models.ChatCompletionsToolCall;
import com.azure.core.annotation.ServiceInterface;

import java.util.List;
import java.util.concurrent.CompletableFuture;

@ServiceInterface(name = "tool-handler")
public interface ToolHandler {
    CompletableFuture<List<String>> handleToolCalls(List<ChatCompletionsToolCall> toolCalls);
}
