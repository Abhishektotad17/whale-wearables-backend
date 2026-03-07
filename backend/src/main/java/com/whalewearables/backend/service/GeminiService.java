package com.whalewearables.backend.service;

import com.google.genai.Client;
import com.google.genai.types.GenerateContentResponse;
import org.springframework.stereotype.Service;

@Service
public class GeminiService {
    private final Client client;
    public GeminiService(Client client) {
        this.client = client;
    }

    public String askGemini(String prompt){

        int maxRetries = 3;
        int delayMs = 1000; // wait 1 second between retries

        for (int attempt = 1; attempt <= maxRetries; attempt++) {
            try {
                GenerateContentResponse response = client.models.generateContent(
                        "gemini-3-flash-preview",
                        prompt,
                        null);
                return response.text();

            } catch (Exception e) {
                boolean isLastAttempt = attempt == maxRetries;
                boolean isServerError = e.getMessage() != null &&
                        (e.getMessage().contains("503") || e.getMessage().contains("high demand"));

                if (isServerError && !isLastAttempt) {
                    try {
                        Thread.sleep(delayMs * attempt); // 1s, 2s, 3s
                    } catch (InterruptedException ie) {
                        Thread.currentThread().interrupt();
                    }
                } else {
                    throw new RuntimeException("Gemini API error: " + e.getMessage(), e);
                }
            }
        }
        throw new RuntimeException("Gemini API failed after " + maxRetries + " attempts.");

//        GenerateContentResponse response =
//                client.models.generateContent(
//                        "gemini-3-flash-preview",
//                        prompt,
//                        null);
//
//        return  (response.text());
    }
}
