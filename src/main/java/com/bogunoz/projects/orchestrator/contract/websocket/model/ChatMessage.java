package com.bogunoz.projects.orchestrator.contract.websocket.model;

import lombok.*;
import org.springframework.modulith.NamedInterface;

@Setter
@Getter
@NamedInterface
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ChatMessage {
    private String sessionId;
    private String content;
    private MessageType messageType;
    private String sender;

}