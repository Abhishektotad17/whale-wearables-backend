package com.whalewearables.backend.dto;

import java.util.List;

public class ChatRequest {
    private String message;
    private String userId;
    private List<ChatHistoryMessage> history;

    public List<ChatHistoryMessage> getHistory() {
        return history;
    }

    public void setHistory(List<ChatHistoryMessage> history) {
        this.history = history;
    }

    public String getMessage() {
        return message;
    }
    public void setMessage(String message) {
        this.message = message;
    }
    public String getUserId() {
        return userId;
    }
    public void setUserId(String userId) {
        this.userId = userId;
    }

    public static class ChatHistoryMessage {
        private String role; // "user" or "bot"
        private String text;

        public String getRole() { return role; }
        public void setRole(String role) { this.role = role; }
        public String getText() { return text; }
        public void setText(String text) { this.text = text; }
    }
}
