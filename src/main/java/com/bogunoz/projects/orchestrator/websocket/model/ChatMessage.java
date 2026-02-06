package com.bogunoz.projects.orchestrator.websocket.model;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ChatMessage {
    private String content;
    private MessageType messageType;
    private String sender;
}