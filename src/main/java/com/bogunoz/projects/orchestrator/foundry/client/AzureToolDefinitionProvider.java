package com.bogunoz.projects.orchestrator.foundry.client;

import com.azure.ai.openai.models.ChatCompletionsFunctionToolDefinition;
import com.azure.ai.openai.models.ChatCompletionsFunctionToolDefinitionFunction;
import com.azure.ai.openai.models.ChatCompletionsToolDefinition;
import com.azure.core.util.BinaryData;
import com.bogunoz.projects.orchestrator.common.constant.Error;
import com.bogunoz.projects.orchestrator.contract.orchestrator.config.OrchestratorProperties;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.List;

@Component
public class AzureToolDefinitionProvider implements ToolDefinitionProvider {

    // region IoC
    private final ObjectMapper OBJECT_MAPPER = new ObjectMapper();
    private final OrchestratorProperties props;
    // endregion IoC

    @Value("classpath:tools/weather.json")
    private Resource weatherToolResource;

    @Value("classpath:tools/search.json")
    private Resource searchToolResource;

    public AzureToolDefinitionProvider(OrchestratorProperties props) {
        this.props = props;
    }

    @Override
    public List<ChatCompletionsToolDefinition> getAvailableTools() {
        return List.of(
            loadToolDefinition(weatherToolResource, props.getToolWeather()),
            loadToolDefinition(searchToolResource, props.getToolSearch())
        );
    }

    private ChatCompletionsFunctionToolDefinition loadToolDefinition(Resource resource, String name) {
        try {
            var root = OBJECT_MAPPER.readTree(resource.getInputStream());

            var description = root.get("description").asText();
            var parameters = BinaryData.fromObject(root.get("parameters"));

            var func = new ChatCompletionsFunctionToolDefinitionFunction(name)
                .setDescription(description)
                .setParameters(parameters);

            return new ChatCompletionsFunctionToolDefinition(func);

        } catch (IOException e) {
            throw new RuntimeException(Error.EXTERNAL_SERVICE_ERROR);
        }
    }
}
