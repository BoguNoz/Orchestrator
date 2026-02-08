package com.bogunoz.projects.orchestrator.foundry.service;

import com.azure.ai.openai.OpenAIClient;
import com.azure.ai.openai.models.ChatCompletionsOptions;
import com.azure.ai.openai.models.ChatRequestSystemMessage;
import com.azure.ai.openai.models.ChatRequestUserMessage;
import com.bogunoz.projects.orchestrator.common.constant.Error;
import com.bogunoz.projects.orchestrator.common.model.Response;
import com.bogunoz.projects.orchestrator.contract.foundry.FoundryChatRequest;
import com.bogunoz.projects.orchestrator.foundry.config.AIClientProperties;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.concurrent.CompletableFuture;

@Service
@Qualifier("openai")
public class OpenAiService implements IAIService {

    // region IoC
    private final OpenAIClient client;
    private final String defaultPrompt;
    private final AIClientProperties props;
    // endregion IoC

    @Autowired
    public OpenAiService(@Value("classpath:prompts/default-prompt.txt") Resource resource,
                         OpenAIClient client,
                         AIClientProperties props) {
        this.client = client;
        this.props = props;
        this.defaultPrompt = readResource(resource);
    }

    private String readResource(Resource resource) {
        try (InputStream is = resource.getInputStream()) {
            return new String(is.readAllBytes(), StandardCharsets.UTF_8);
        } catch (IOException e) {
            throw new IllegalStateException(Error.CANNOT_LOAD_PROMPT, e);
        }
    }

    @Async
    public CompletableFuture<Response<String>> askChatAsync(FoundryChatRequest request) {
        var systemPrompt = buildSystemPrompt(request.context());

        var chatMessages = List.of(
                new ChatRequestSystemMessage(systemPrompt),
                new ChatRequestUserMessage(request.message())
        );


        var options = new ChatCompletionsOptions(chatMessages);
        options.setMaxTokens(props.getMaxTokens());
        options.setTemperature(props.getTemperature());
        options.setTopP(props.getTopP());

        var result = client.getChatCompletions(
                props.getDeploymentName(),
                options
        );

        var answer = result.getChoices()
                .get(0)
                .getMessage()
                .getContent();

        return CompletableFuture.completedFuture(Response.ok(answer));
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
