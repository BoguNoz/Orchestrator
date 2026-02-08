package com.bogunoz.projects.orchestrator.foundry.service;

import com.azure.ai.openai.OpenAIClient;
import com.azure.ai.openai.models.ChatCompletionsOptions;
import com.azure.ai.openai.models.ChatRequestSystemMessage;
import com.azure.ai.openai.models.ChatRequestUserMessage;
import com.bogunoz.projects.orchestrator.common.constant.Error;
import com.bogunoz.projects.orchestrator.common.model.Response;
import com.bogunoz.projects.orchestrator.contract.foundry.FoundryChatRequest;
import com.bogunoz.projects.orchestrator.foundry.config.OpenAIClientProperties;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.List;

@Service
public class FoundryService implements IFoundryService {

    // region IoC
    private final OpenAIClient client;
    private final String defaultPrompt;
    private final OpenAIClientProperties props;
    // endregion IoC

    public FoundryService(@Value("classpath:prompts/default-prompt.txt") Resource resource,
                          OpenAIClient client,
                          OpenAIClientProperties props) {
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

    public Response<String> askChat(FoundryChatRequest request) {
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

        return Response.ok(answer);
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
