package com.bogunoz.projects.orchestrator.orchestrator.handler;

import com.azure.ai.openai.models.ChatCompletionsFunctionToolCall;
import com.azure.ai.openai.models.ChatCompletionsToolCall;
import com.bogunoz.projects.orchestrator.common.constant.Error;
import com.bogunoz.projects.orchestrator.contract.orchestrator.config.OrchestratorProperties;
import com.bogunoz.projects.orchestrator.contract.weather.WeatherForecastRequest;
import com.bogunoz.projects.orchestrator.orchestrator.model.SearchArgs;
import com.bogunoz.projects.orchestrator.orchestrator.model.WeatherArgs;
import com.bogunoz.projects.orchestrator.search.service.SearchService;
import com.bogunoz.projects.orchestrator.weather.service.WeatherService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.concurrent.CompletableFuture;

@Component
public class AzureToolHandler implements  ToolHandler{

    // region IoC
    private final WeatherService weatherService;
    private final SearchService searchService;
    private final OrchestratorProperties props;

    private final ObjectMapper OBJECT_MAPPER = new ObjectMapper();
    // endregion IoC

    public AzureToolHandler(WeatherService weatherService, SearchService searchService, OrchestratorProperties props) {
        this.weatherService = weatherService;
        this.searchService = searchService;
        this.props = props;
    }

    @Override
    public CompletableFuture<List<String>> handleToolCalls(List<ChatCompletionsToolCall> toolCalls) {
        List<CompletableFuture<String>> futures = toolCalls.stream()
                .map(toolCall -> {
                    var functionCall = ((ChatCompletionsFunctionToolCall) toolCall);
                    String callId = functionCall.getId();
                    String functionName = functionCall.getFunction().getName();
                    String jsonArgs = functionCall.getFunction().getArguments();

                    if (functionName.equals(props.getToolWeather())) {
                        return handleWeatherToolCall(jsonArgs, callId);
                    }
                    if (functionName.equals(props.getToolSearch())) {
                        return handleSearchToolCall(jsonArgs, callId);
                    }
                    return CompletableFuture.completedFuture(formatToolResult(callId, com.bogunoz.projects.orchestrator.common.constant.Error.SEBASTIAN_ERROR_TOOL));

                })
                .toList();

        return CompletableFuture.allOf(futures.toArray(new CompletableFuture[0]))
                .thenApply(v -> futures.stream().map(CompletableFuture::join).toList());
    }

    private CompletableFuture<String> handleWeatherToolCall (String jsonArgs, String callId) {
        try {
            var args = OBJECT_MAPPER.readValue(jsonArgs, WeatherArgs.class);
            var weatherRequest = new WeatherForecastRequest();
            weatherRequest.setUseCityName(true);
            weatherRequest.setCityName(args.location());

            return weatherService.getWeatherForecastAsync(weatherRequest)
                    .thenApply(res ->
                            formatToolResult(callId, res.getData().toString()));
        } catch (Exception e) {
            return CompletableFuture.completedFuture(formatToolResult(callId, Error.SEBASTIAN_FATAL_ERROR));
        }
    }

    private CompletableFuture<String> handleSearchToolCall (String jsonArgs, String callId) {
        try {
            var args = OBJECT_MAPPER.readValue(jsonArgs, SearchArgs.class);
            return searchService.searchForAsync(args.searchKeys())
                    .thenApply(res ->
                            formatToolResult(callId, res.getData()));
        } catch (Exception e) {
            return CompletableFuture.completedFuture(formatToolResult(callId, Error.SEBASTIAN_FATAL_ERROR));
        }
    }

    private String formatToolResult(String callId, String result) {
        return "TOOL_ID: " + callId + " RESULT: " + result;
    }
}
