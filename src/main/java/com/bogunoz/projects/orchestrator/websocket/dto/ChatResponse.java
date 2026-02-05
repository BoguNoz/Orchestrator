package com.bogunoz.projects.orchestrator.websocket.dto;

import com.fasterxml.jackson.annotation.JsonFormat;

import java.time.LocalDateTime;

public class ChatResponse {
    private final String replyContent;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private final LocalDateTime timestamp;

    public ChatResponse(String replyContent) {
        this.replyContent = replyContent;
        this.timestamp = LocalDateTime.now();
    }

    public String getReplyContent() {
        return replyContent;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }
}
