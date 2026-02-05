package com.bogunoz.projects.orchestrator.websocket.dto;

public class ChatRequest {
    private String messageContent;

    public String getMessageContent() {
        return messageContent;
    }

    public void setMessageContent(String messageContent) {
        this.messageContent = messageContent;
    }
}