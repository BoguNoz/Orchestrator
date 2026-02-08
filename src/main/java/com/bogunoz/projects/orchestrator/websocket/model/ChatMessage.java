package com.bogunoz.projects.orchestrator.websocket.model;

import lombok.Builder;

@Builder
public record ChatMessage (
    String content,
    MessageType messageType,
    String sender
) {}