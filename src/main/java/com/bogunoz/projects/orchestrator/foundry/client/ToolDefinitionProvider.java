package com.bogunoz.projects.orchestrator.foundry.client;

import com.azure.ai.openai.models.ChatCompletionsToolDefinition;
import com.azure.core.annotation.ServiceInterface;

import java.util.List;

@ServiceInterface(name = "tool-client")
public interface ToolDefinitionProvider {
    List<ChatCompletionsToolDefinition> getAvailableTools();
}
