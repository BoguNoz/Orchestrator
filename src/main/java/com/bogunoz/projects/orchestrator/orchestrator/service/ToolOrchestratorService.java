package com.bogunoz.projects.orchestrator.orchestrator.service;

import com.azure.ai.openai.models.ChatCompletionsFunctionToolCall;
import com.azure.ai.openai.models.ChatCompletionsToolCall;
import com.bogunoz.projects.orchestrator.common.constant.Error;
import com.bogunoz.projects.orchestrator.common.model.Response;
import com.bogunoz.projects.orchestrator.contract.foundry.FoundryChatRequest;
import com.bogunoz.projects.orchestrator.contract.orchestrator.config.OrchestratorProperties;
import com.bogunoz.projects.orchestrator.contract.weather.WeatherForecastRequest;
import com.bogunoz.projects.orchestrator.foundry.service.IAIService;
import com.bogunoz.projects.orchestrator.orchestrator.model.SearchArgs;
import com.bogunoz.projects.orchestrator.orchestrator.model.WeatherArgs;
import com.bogunoz.projects.orchestrator.search.service.ISearchService;
import com.bogunoz.projects.orchestrator.weather.service.IWeatherService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.CompletableFuture;

@Service
public class ToolOrchestratorService implements IToolOrchestratorService {
    // region IoC
    private final IAIService aiService;
    private final IWeatherService weatherService;
    private final ISearchService searchService;
    private final OrchestratorProperties props;
    private final ObjectMapper OBJECT_MAPPER = new ObjectMapper();
    // endregion IoC

    public ToolOrchestratorService(IAIService aiService, IWeatherService weatherService, ISearchService searchService, OrchestratorProperties props) {
        this.aiService = aiService;
        this.weatherService = weatherService;
        this.searchService = searchService;
        this.props = props;
    }

    @Override
    public CompletableFuture<String> processQuery(FoundryChatRequest request) {

        return aiService.askChatAsync(request).thenCompose(response -> {
               var message = response.getData();
               var toolCalls = message.getToolCalls();

               if(toolCalls == null || toolCalls.isEmpty()) {
                   return CompletableFuture.completedFuture(message.getContent());
               }

               return handleToolCalls(toolCalls)
                       .thenCompose(toolResultsAsContext -> {
                           var enrichedRequest = new FoundryChatRequest(
                                   request.message(),
                                   toolResultsAsContext
                           );
                           return processQuery(enrichedRequest);
                       });
            });
    }

    private CompletableFuture<List<String>> handleToolCalls(List<ChatCompletionsToolCall> toolCalls) {
        List<CompletableFuture<String>> futures = toolCalls.stream()
                .map(toolCall -> {
                    var functionCall = ((ChatCompletionsFunctionToolCall) toolCall).getFunction();
                    String functionName = functionCall.getName();
                    String jsonArgs = functionCall.getArguments();

                    try {
                        if (functionName.equals(props.getToolWeather())) {
                            var args = OBJECT_MAPPER.readValue(jsonArgs, WeatherArgs.class);
                            var weatherRequest = new WeatherForecastRequest();
                            weatherRequest.setUseCityName(true);
                            weatherRequest.setCityName(args.location());

                            return weatherService.getWeatherForecastAsync(weatherRequest)
                                    .thenApply(res -> "Dane pogodowe dla " + args.location() + ": " + res.getData().toString());
                        }

                        else if (functionName.equals(props.getToolSearch())) {
                            var args = OBJECT_MAPPER.readValue(jsonArgs, SearchArgs.class);

                            return searchService.searchForAsync(args.searchKeys())
                                    .thenApply(res -> "Wyniki wyszukiwania: " + res.getData());
                        }

                        return CompletableFuture.completedFuture(Error.EXTERNAL_SERVICE_ERROR);

                    } catch (Exception e) {
                        return CompletableFuture.completedFuture(Error.EXTERNAL_SERVICE_ERROR);
                    }
                })
                .toList();

        return CompletableFuture.allOf(futures.toArray(new CompletableFuture[0]))
                .thenApply(v -> futures.stream()
                        .map(CompletableFuture::join)
                        .toList()
                );
    }
}
