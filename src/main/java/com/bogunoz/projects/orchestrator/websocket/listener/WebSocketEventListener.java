package com.bogunoz.projects.orchestrator.websocket.listener;

import com.bogunoz.projects.orchestrator.websocket.config.WebSocketProperties;
import com.bogunoz.projects.orchestrator.contract.websocket.model.ChatMessage;
import com.bogunoz.projects.orchestrator.contract.websocket.model.MessageType;
import io.micrometer.common.util.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.event.EventListener;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.stereotype.Component;
import org.springframework.util.ObjectUtils;
import org.springframework.web.socket.messaging.SessionConnectedEvent;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;

@Component
public class WebSocketEventListener {

    //region IoC
    private static final Logger log =
            LoggerFactory.getLogger(WebSocketEventListener.class);
    private final SimpMessageSendingOperations messagingTemplate;
    private final WebSocketProperties props;
    //endregion IoC

    public WebSocketEventListener(SimpMessageSendingOperations messagingTemplate, WebSocketProperties props) {
        this.messagingTemplate = messagingTemplate;
        this.props = props;
    }

    @EventListener
    public void handleWebSocketConnectListener(SessionConnectedEvent event) {
        //TODO Add Redis
        log.info("Received a new web socket connection");
    }

    @EventListener
    public void handleWebSocketDisconnectListener(SessionDisconnectEvent event) {
        StompHeaderAccessor headerAccessor = StompHeaderAccessor.wrap(event.getMessage());
        var username = ObjectUtils.nullSafeToString(headerAccessor.getSessionAttributes().get("username"));

        if (StringUtils.isNotBlank(username)) {
            log.info("user disconnected: {}", username);
        }

        var chatMessage = ChatMessage.builder()
                .messageType(MessageType.LEAVE)
                .sender(username)
                .build();

        messagingTemplate.convertAndSend(props.getBroker().getPublicTopic(), chatMessage);

    }

}
