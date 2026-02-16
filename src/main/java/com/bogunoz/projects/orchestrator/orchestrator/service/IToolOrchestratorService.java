package com.bogunoz.projects.orchestrator.orchestrator.service;

import com.azure.core.annotation.ServiceInterface;
import com.bogunoz.projects.orchestrator.common.model.Response;
import com.bogunoz.projects.orchestrator.contract.foundry.FoundryChatRequest;
import org.springframework.modulith.NamedInterface;

import java.util.concurrent.CompletableFuture;

@NamedInterface
@ServiceInterface(name = "tool")
public interface IToolOrchestratorService {
    CompletableFuture<String> processQuery(FoundryChatRequest request);
}
