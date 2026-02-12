package com.bogunoz.projects.orchestrator.websocket.controller;

import com.bogunoz.projects.orchestrator.common.constant.Error;
import com.bogunoz.projects.orchestrator.contract.foundry.FoundryChatRequest;
import com.bogunoz.projects.orchestrator.orchestrator.service.IToolOrchestratorService;
import com.bogunoz.projects.orchestrator.websocket.config.WebSocketProperties;
import com.bogunoz.projects.orchestrator.websocket.model.ChatMessage;
import com.bogunoz.projects.orchestrator.websocket.model.MessageType;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

import java.util.ArrayList;

@Controller
public class ChatController {

    // region IoC
    private final SimpMessagingTemplate messagingTemplate;
    private final WebSocketProperties props;
    private final IToolOrchestratorService orchestratorService;
    // endregion IoC

    public ChatController(SimpMessagingTemplate messagingTemplate, WebSocketProperties props, IToolOrchestratorService orchestratorService) {
        this.messagingTemplate = messagingTemplate;
        this.props = props;
        this.orchestratorService = orchestratorService;
    }

    @MessageMapping("/sendMessage")
    public void sendMessage(@Payload ChatMessage chatMessage) {
        var aiRequest = new FoundryChatRequest(
                chatMessage.content(),
                new ArrayList<>()
        );

        messagingTemplate.convertAndSend(
                props.getBroker().getPublicTopic(),
                chatMessage
        );

        orchestratorService.processQuery(aiRequest)
                .thenAccept(aiAnswer -> {
                    var response = ChatMessage.builder()
                            .messageType(MessageType.CHAT)
                            .sender("Sebastian")
                            .content(aiAnswer)
                            .build();

                    messagingTemplate.convertAndSend(
                            props.getBroker().getPublicTopic(),
                            response
                    );
                })
                .exceptionally(ex -> {
                    messagingTemplate.convertAndSend(
                            props.getBroker().getPublicTopic(),
                            ChatMessage.builder()
                                    .content(Error.SEBASTIAN_FATAL_ERROR)
                                    .messageType(MessageType.CHAT)
                                    .sender("Sebastian").build()
                    );

                    return null;
                });
    }
}
