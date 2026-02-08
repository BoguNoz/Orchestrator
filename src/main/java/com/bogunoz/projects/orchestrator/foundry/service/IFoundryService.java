package com.bogunoz.projects.orchestrator.foundry.service;

import com.azure.core.annotation.ServiceInterface;
import com.bogunoz.projects.orchestrator.common.model.Response;
import com.bogunoz.projects.orchestrator.contract.foundry.FoundryChatRequest;
import org.springframework.modulith.NamedInterface;

@NamedInterface
@ServiceInterface(name = "foundry")
public interface IFoundryService {
    Response<String> askChat(FoundryChatRequest request);
}
