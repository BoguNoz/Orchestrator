package com.bogunoz.projects.orchestrator.foundry.config;

import com.azure.ai.openai.OpenAIClient;
import com.azure.ai.openai.OpenAIClientBuilder;
import com.azure.core.credential.AzureKeyCredential;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableConfigurationProperties(OpenAIClientProperties.class)
public class OpenAIClientConfig {
    @Bean
    public OpenAIClient openAIClient(OpenAIClientProperties props) {
        return new OpenAIClientBuilder()
                .credential(new AzureKeyCredential(props.getKey()))
                .endpoint(props.getEndpoint())
                .buildClient();
    }
}
