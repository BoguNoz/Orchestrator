package com.bogunoz.projects.orchestrator.contract.websocket.model;

import lombok.Builder;
import org.springframework.modulith.NamedInterface;

@NamedInterface
@Builder
public record ChatMessage (
    String content,
    MessageType messageType,
    String sender
) {}