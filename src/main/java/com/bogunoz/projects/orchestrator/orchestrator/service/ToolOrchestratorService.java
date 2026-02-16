package com.bogunoz.projects.orchestrator.orchestrator.service;

import com.azure.ai.openai.models.ChatCompletionsFunctionToolCall;
import com.azure.ai.openai.models.ChatCompletionsToolCall;
import com.bogunoz.projects.orchestrator.common.constant.Error;
import com.bogunoz.projects.orchestrator.contract.foundry.FoundryChatRequest;
import com.bogunoz.projects.orchestrator.contract.orchestrator.config.OrchestratorProperties;
import com.bogunoz.projects.orchestrator.contract.weather.WeatherForecastRequest;
import com.bogunoz.projects.orchestrator.foundry.service.AIService;
import com.bogunoz.projects.orchestrator.orchestrator.handler.ToolHandler;
import com.bogunoz.projects.orchestrator.orchestrator.model.SearchArgs;
import com.bogunoz.projects.orchestrator.orchestrator.model.WeatherArgs;
import com.bogunoz.projects.orchestrator.search.service.SearchService;
import com.bogunoz.projects.orchestrator.weather.service.WeatherService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

@Service
public class ToolOrchestratorService implements IToolOrchestratorService {
    // region IoC
    private final AIService aiService;
    private final ToolHandler toolHandler;
    // endregion IoC

    public ToolOrchestratorService(AIService aiService, ToolHandler toolHandler) {
        this.aiService = aiService;
        this.toolHandler = toolHandler;
    }

    @Override
    public CompletableFuture<String> processQuery(FoundryChatRequest request) {
        return aiService.askChatAsync(request).thenCompose(response -> {
            var message = response.getData();
            var toolCalls = message.getToolCalls();

            if (toolCalls == null || toolCalls.isEmpty()) {
                return CompletableFuture.completedFuture(message.getContent());
            }

            return toolHandler.handleToolCalls(toolCalls)
                    .thenCompose(toolResults -> {
                        List<String> updatedContext = new ArrayList<>(request.context());
                        updatedContext.addAll(toolResults);

                        var nextRequest = new FoundryChatRequest(
                                request.message(),
                                updatedContext
                        );

                        return processQuery(nextRequest);
                    });
        });
    }
}
