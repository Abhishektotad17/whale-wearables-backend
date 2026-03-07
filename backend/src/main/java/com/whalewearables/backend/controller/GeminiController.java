package com.whalewearables.backend.controller;

import com.whalewearables.backend.dto.ChatRequest;
import com.whalewearables.backend.dto.ChatResponse;
import com.whalewearables.backend.service.GeminiService;
import com.whalewearables.backend.service.ProductService;
import com.whalewearables.backend.service.RateLimiterService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/gemini")
public class GeminiController {
    @Autowired
    private GeminiService geminiService;
    @Autowired
    private ProductService productService;
    @Autowired
    private RateLimiterService rateLimiterService;

    private final String systemPrompt = """
            You are NextGen AI, a smart assistant for Nextgen Wearables — a wearable tech e-commerce platform.

            You have three core capabilities:

            1. CONVERSATIONAL PRODUCT SEARCH
               - Help users find products by describing what they're looking for
               - Ask clarifying questions (budget, use case, style) when needed
               - Reference the product catalog provided to give accurate results
               - Format product results clearly with name, price, and a short description

            2. PERSONALIZED RECOMMENDATIONS
               - Suggest products based on user preferences, past questions, or stated needs
               - Cross-sell and upsell naturally (e.g., "You might also like...")
               - Consider the conversation history to make contextual suggestions

            3. CUSTOMER FAQ ASSISTANCE
               - Answer questions about: shipping, returns, sizing, warranties, payment methods
               - Shipping: Standard 5-7 days (₹49), Express 2-3 days (₹99), Free over ₹1000
               - Returns: 30-day hassle-free returns, items must be unused with original packaging
               - Sizing: Wearables are one-size-fits-most with adjustable straps; apparel follows standard US sizing
               - Warranty: 1-year manufacturer warranty on all electronics
               - Payment: Visa, Mastercard, PayPal, Apple Pay, Google Pay

            Always be friendly, concise, and helpful. If you don't know something, say so honestly.
            Always display all prices in Indian Rupees (₹).
            When listing products, use this format:
            **[Product Name]** — ₹[Price]
            [One-line description]
            """;

@PostMapping("/ask")
public ResponseEntity<ChatResponse>  askGeminiApi(@RequestBody ChatRequest chatRequest) {

    try{
        // ✅ Resolve user identity — use userId if logged in, else fall back to "guest"
        String userKey = (chatRequest.getUserId() != null && !chatRequest.getUserId().isBlank())
                ? chatRequest.getUserId()
                : "guest";

        // ✅ Check rate limit
        if (!rateLimiterService.tryConsume(userKey)) {
            return ResponseEntity
                    .status(HttpStatus.TOO_MANY_REQUESTS)
                    .body(new ChatResponse("You're sending too many messages. Please wait a moment and try again. 🙏"));
        }

        // Build conversation history string
        StringBuilder historyBuilder = new StringBuilder();
        if (chatRequest.getHistory() != null && !chatRequest.getHistory().isEmpty()) {
            historyBuilder.append("\n--- Conversation History ---\n");
            for (ChatRequest.ChatHistoryMessage msg : chatRequest.getHistory()) {
                String label = msg.getRole().equals("user") ? "User" : "Assistant";
                historyBuilder.append(label).append(": ").append(msg.getText()).append("\n");
            }
            historyBuilder.append("--- End of History ---\n");
        }

        String productContext = productService.getProductCatalogSummary();

        String fullPrompt = systemPrompt
                + "\n\n--- Product Catalog ---\n" + productContext
                + historyBuilder
                + "\nUser: " + chatRequest.getMessage()
                + "\nAssistant:";

        String reply = geminiService.askGemini(fullPrompt);
        return ResponseEntity.ok(new ChatResponse(reply));
    }
    catch(RuntimeException e){
        String userMessage = e.getMessage().contains("high demand") || e.getMessage().contains("503")
                ? "I'm experiencing high demand right now. Please try again in a few seconds! ⏳"
                : "Something went wrong on my end. Please try again! 🙏";

        return ResponseEntity
                .status(HttpStatus.SERVICE_UNAVAILABLE)
                .body(new ChatResponse(userMessage));
        }
    }
}

