package com.bogunoz.projects.orchestrator.websocket.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Getter
@ConfigurationProperties(prefix = "websocket")
public class WebSocketProperties {

    private Broker broker = new Broker();
    private Application application = new Application();
    private Endpoint endpoint = new Endpoint();

    @Getter
    @Setter
    public static class Broker {
        private String destinationPrefix;
        private String publicTopic;
    }

    @Getter
    @Setter
    public static class Application {
        private String destinationPrefix;
    }

    @Getter
    @Setter
    public static class Endpoint {
        private String chat;
    }
}
