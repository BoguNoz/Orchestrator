package com.bogunoz.projects.orchestrator.contract.foundry;

import org.springframework.modulith.NamedInterface;

import java.util.List;

@NamedInterface
public record FoundryChatRequest(
    String message,
    List<String> context
) {}
