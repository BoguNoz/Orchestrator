package com.bogunoz.projects.orchestrator.foundry.service;

import com.azure.ai.openai.OpenAIClient;
import com.azure.ai.openai.models.*;
import com.azure.core.util.BinaryData;
import com.bogunoz.projects.orchestrator.common.constant.Error;
import com.bogunoz.projects.orchestrator.common.model.Response;
import com.bogunoz.projects.orchestrator.contract.foundry.FoundryChatRequest;
import com.bogunoz.projects.orchestrator.contract.orchestrator.config.OrchestratorProperties;
import com.bogunoz.projects.orchestrator.foundry.config.AIClientProperties;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

@Service
@Qualifier("openai")
public class OpenAiService implements IAIService {

    // region IoC
    private final OpenAIClient client;
    private final String defaultPrompt;
    private final AIClientProperties aiProps;
    private final OrchestratorProperties orchestratorProp;
    // endregion IoC

    public OpenAiService(@Value("classpath:prompts/default-prompt.txt") Resource resource,
                         OpenAIClient client,
                         AIClientProperties aiProps, OrchestratorProperties orchestratorProp) {
        this.client = client;
        this.aiProps = aiProps;
        this.defaultPrompt = readResource(resource);
        this.orchestratorProp = orchestratorProp;
    }

    private String readResource(Resource resource) {
        try (InputStream is = resource.getInputStream()) {
            return new String(is.readAllBytes(), StandardCharsets.UTF_8);
        } catch (IOException e) {
            throw new IllegalStateException(Error.CANNOT_LOAD_PROMPT, e);
        }
    }

    @Async
    public CompletableFuture<Response<ChatResponseMessage>> askChatAsync(FoundryChatRequest request) {
        var systemPrompt = buildSystemPrompt(request.context());

        List<ChatRequestMessage> chatMessages = new ArrayList<>();
        chatMessages.add(new ChatRequestSystemMessage(systemPrompt));
        chatMessages.add(new ChatRequestUserMessage(request.message()));

        var options = new ChatCompletionsOptions(chatMessages);
        options.setMaxTokens(aiProps.getMaxTokens());
        options.setTemperature(aiProps.getTemperature());
        options.setTools(getAvailableTools());
        options.setTopP(aiProps.getTopP());

        var result = client.getChatCompletions(
                aiProps.getDeploymentName(),
                options
        );

        var message = result.getChoices().get(0).getMessage();
        return CompletableFuture.completedFuture(Response.ok(message));
    }

    private List<ChatCompletionsToolDefinition> getAvailableTools() {
        ChatCompletionsFunctionToolDefinitionFunction weatherFunc =
                new ChatCompletionsFunctionToolDefinitionFunction(orchestratorProp.getToolWeather())
                        .setDescription("Pobiera aktualną prognozę pogody dla danej lokalizacji.")
                        .setParameters(BinaryData.fromObject(Map.of(
                                "type", "object",
                                "properties", Map.of(
                                        "location", Map.of(
                                                "type", "string",
                                                "description", "Miasto i kraj, np. Warszawa, Polska"
                                        )
                                ),
                                "required", List.of("location")
                        )));

        ChatCompletionsFunctionToolDefinitionFunction searchFunc =
                new ChatCompletionsFunctionToolDefinitionFunction(orchestratorProp.getToolSearch())
                        .setDescription("Przeszukuje internet w poszukiwaniu najnowszych informacji i wiadomości.")
                        .setParameters(BinaryData.fromObject(Map.of(
                                "type", "object",
                                "properties", Map.of(
                                        "searchKeys", Map.of(
                                                "type", "array",
                                                "items", Map.of("type", "string"),
                                                "description", "Lista słów kluczowych do wyszukania"
                                        )
                                ),
                                "required", List.of("searchKeys")
                        )));

        return List.of(
                new ChatCompletionsFunctionToolDefinition(weatherFunc),
                new ChatCompletionsFunctionToolDefinition(searchFunc)
        );
    }

    private String buildSystemPrompt(List<String> context) {
        var sb = new StringBuilder();
        sb.append(defaultPrompt.trim());

        if (context != null && !context.isEmpty()) {
            sb.append("\n\nContext:\n");
            for (String ctx : context) {
                sb.append("- ").append(ctx).append("\n");
            }
        }

        return sb.toString();
    }
}
