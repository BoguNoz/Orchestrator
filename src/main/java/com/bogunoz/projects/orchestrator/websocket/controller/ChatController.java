package com.bogunoz.projects.orchestrator.websocket.controller;

import com.bogunoz.projects.orchestrator.cache.service.MessageCacheService;
import com.bogunoz.projects.orchestrator.common.constant.Chat;
import com.bogunoz.projects.orchestrator.common.constant.Error;
import com.bogunoz.projects.orchestrator.contract.foundry.FoundryChatRequest;
import com.bogunoz.projects.orchestrator.orchestrator.service.IToolOrchestratorService;
import com.bogunoz.projects.orchestrator.websocket.config.WebSocketProperties;
import com.bogunoz.projects.orchestrator.contract.websocket.model.ChatMessage;
import com.bogunoz.projects.orchestrator.contract.websocket.model.MessageType;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

import java.util.List;


@Controller
public class ChatController {

    // region IoC
    private final SimpMessagingTemplate messagingTemplate;
    private final WebSocketProperties props;
    private final IToolOrchestratorService orchestratorService;
    private final MessageCacheService messageCacheService;
    // endregion IoC

    public ChatController(SimpMessagingTemplate messagingTemplate, WebSocketProperties props, IToolOrchestratorService orchestratorService, MessageCacheService messageCacheService) {
        this.messagingTemplate = messagingTemplate;
        this.props = props;
        this.orchestratorService = orchestratorService;
        this.messageCacheService = messageCacheService;
    }

    @MessageMapping("/sendMessage")
    public void sendMessage(@Payload ChatMessage chatMessage,  SimpMessageHeaderAccessor headerAccessor) {
        var sessionId =  headerAccessor.getSessionId();
        chatMessage.setSessionId(sessionId);

        messageCacheService.addMessage(chatMessage).join();
        messagingTemplate.convertAndSend(
                props.getBroker().getPublicTopic(),
                chatMessage
        );

        var chatHistory = messageCacheService.getAllMessages(sessionId)
                .thenApply(messages ->
                        messages.stream().map(ChatMessage::getContent).toList())
                .join();

        var request = new FoundryChatRequest(
                chatMessage.getContent(),
                chatHistory
        );

        addResponseMessage(request, sessionId);
    }

    private void addResponseMessage(FoundryChatRequest request, String sessionId) {
        orchestratorService.processQuery(request)
                .thenAccept(answer -> {
                    var response = ChatMessage.builder()
                            .sessionId(sessionId)
                            .messageType(MessageType.CHAT)
                            .sender(Chat.CHAT_AGENT_NAME)
                            .content(answer)
                            .build();
                    messageCacheService.addMessage(response);

                    messagingTemplate.convertAndSend(
                            props.getBroker().getPublicTopic(),
                            response
                    );
                })
                .exceptionally(ex -> {
                    var response = ChatMessage.builder()
                            .sessionId(sessionId)
                            .content(Error.SEBASTIAN_FATAL_ERROR)
                            .messageType(MessageType.CHAT)
                            .sender(Chat.CHAT_AGENT_NAME).
                            build();
                    messageCacheService.addMessage(response);

                    messagingTemplate.convertAndSend(
                            props.getBroker().getPublicTopic(),
                            response
                    );

                    return null;
                });
    }
}
