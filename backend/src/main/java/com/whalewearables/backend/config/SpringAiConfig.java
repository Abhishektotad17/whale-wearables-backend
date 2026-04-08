package com.whalewearables.backend.config;

import com.google.genai.Client;
import org.springframework.ai.embedding.EmbeddingModel;
import org.springframework.ai.google.genai.GoogleGenAiEmbeddingConnectionDetails;
import org.springframework.ai.google.genai.text.GoogleGenAiTextEmbeddingModel;
import org.springframework.ai.google.genai.text.GoogleGenAiTextEmbeddingOptions;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;


@Configuration
public class SpringAiConfig {

    @Value("${gemini.api.key}")
    private String apiKey;

    // ✅ Expose connection details as a bean
    @Bean
    public GoogleGenAiEmbeddingConnectionDetails googleGenAiEmbeddingConnectionDetails() {
        return GoogleGenAiEmbeddingConnectionDetails.builder()
                .apiKey(apiKey)
                .build();
    }

    // ✅ Expose embedding model as a bean
    @Bean
    public EmbeddingModel embeddingModel(GoogleGenAiEmbeddingConnectionDetails connectionDetails) {
        GoogleGenAiTextEmbeddingOptions options =
                GoogleGenAiTextEmbeddingOptions.builder()
                        .model("text-embedding-004")
                        .build();
        return new GoogleGenAiTextEmbeddingModel(connectionDetails, options);
    }
}
