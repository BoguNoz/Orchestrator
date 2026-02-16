package com.bogunoz.projects.orchestrator.foundry.service;

import com.azure.ai.openai.OpenAIClient;
import com.azure.ai.openai.models.*;
import com.bogunoz.projects.orchestrator.common.constant.Error;
import com.bogunoz.projects.orchestrator.common.model.Response;
import com.bogunoz.projects.orchestrator.contract.foundry.FoundryChatRequest;
import com.bogunoz.projects.orchestrator.contract.orchestrator.config.OrchestratorProperties;
import com.bogunoz.projects.orchestrator.foundry.client.ToolDefinitionProvider;
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
import java.util.concurrent.CompletableFuture;

@Service
@Qualifier("openai")
public class OpenAiService implements AIService {

    // region IoC
    private final OpenAIClient client;
    private final String defaultPrompt;
    private final AIClientProperties props;
    private final ToolDefinitionProvider toolClient;
    // endregion IoC

    public OpenAiService(@Value("classpath:prompts/default-prompt.txt") Resource resource,
                         OpenAIClient client,
                         AIClientProperties aiProps, OrchestratorProperties orchestratorProp, ToolDefinitionProvider toolClient) {
        this.client = client;
        this.props = aiProps;
        this.defaultPrompt = readResource(resource);
        this.toolClient = toolClient;
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
        options.setMaxTokens(props.getMaxTokens());
        options.setTemperature(props.getTemperature());
        options.setTools(toolClient.getAvailableTools());
        options.setTopP(props.getTopP());

        var result = client.getChatCompletions(
                props.getDeploymentName(),
                options
        );

        var message = result.getChoices().get(0).getMessage();
        return CompletableFuture.completedFuture(Response.ok(message));
    }

    private String buildSystemPrompt(List<String> context) {
        var sb = new StringBuilder();
        sb.append(defaultPrompt.trim());

        if (context != null && !context.isEmpty()) {
            sb.append(props.getBuildPromptContext());
            for (String ctx : context) {
                sb.append("- ").append(ctx).append("\n");
            }
        }
        return sb.toString();
    }
}
