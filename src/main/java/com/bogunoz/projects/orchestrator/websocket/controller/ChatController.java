package com.bogunoz.projects.orchestrator.websocket.controller;

import com.bogunoz.projects.orchestrator.websocket.config.WebSocketProperties;
import com.bogunoz.projects.orchestrator.websocket.model.ChatMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

@Controller
public class ChatController {

    private final SimpMessagingTemplate messagingTemplate;
    private final WebSocketProperties props;

    @Autowired
    public ChatController(SimpMessagingTemplate messagingTemplate, WebSocketProperties props) {
        this.messagingTemplate = messagingTemplate;
        this.props = props;
    }

    @MessageMapping("/sendMessage")
    public void sendMessage(@Payload ChatMessage chatMessage) {
        System.out.println("RECEIVED: " + chatMessage);
        // TODO Add call to llm
        messagingTemplate.convertAndSend(

                props.getBroker().getPublicTopic(),
                chatMessage // TODO Switch with correct message
        );
    }
}
