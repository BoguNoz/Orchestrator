package com.bogunoz.projects.orchestrator.websocket.config;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

@Configuration
@EnableWebSocketMessageBroker
@EnableConfigurationProperties(WebSocketProperties.class)
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

    // region IoC
    private final WebSocketProperties props;
    // endregion IoC

    public WebSocketConfig(WebSocketProperties props) {
        this.props = props;
    }

    @Override
    public void configureMessageBroker(final MessageBrokerRegistry config) {
        config.enableSimpleBroker(
                props.getBroker().getDestinationPrefix());
        config.setApplicationDestinationPrefixes(
                props.getApplication().getDestinationPrefix());
    }

    @Override
    public void registerStompEndpoints(final StompEndpointRegistry registry) {
        registry.addEndpoint(
                props.getEndpoint().getChat()
        ).withSockJS();
    }
}
