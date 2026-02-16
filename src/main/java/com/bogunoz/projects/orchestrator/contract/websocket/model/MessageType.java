package com.bogunoz.projects.orchestrator.contract.websocket.model;

import org.springframework.modulith.NamedInterface;

@NamedInterface
public enum MessageType {
    CHAT,
    JOIN,
    LEAVE
}
