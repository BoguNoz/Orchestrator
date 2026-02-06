package com.bogunoz.projects.orchestrator.fundry.config;

import com.azure.ai.openai.OpenAIClient;
import com.azure.ai.openai.OpenAIClientBuilder;
import com.azure.core.credential.AzureKeyCredential;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableConfigurationProperties(OpenAIClientProperties.class)
public class OpenAIClientConfig {

    // region IoC
    private final OpenAIClientProperties props;
    // endregion IoC


    public OpenAIClientConfig(OpenAIClientProperties props) {
        this.props = props;
    }

    @Bean
    public OpenAIClient openAIClient(OpenAIClientProperties props) {
        return new OpenAIClientBuilder()
                .credential(new AzureKeyCredential(props.getApiKey()))
                .endpoint(props.getEndpoint())
                .buildClient();
    }
}
