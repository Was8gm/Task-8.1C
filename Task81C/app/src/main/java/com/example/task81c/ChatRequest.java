package com.example.task81c;

import java.util.List;

public class ChatRequest {
    private String userMessage;
    private List<ChatHistoryItem> chatHistory;

    public ChatRequest(String userMessage, List<ChatHistoryItem> chatHistory) {
        this.userMessage = userMessage;
        this.chatHistory = chatHistory;
    }

    public String getUserMessage() {
        return userMessage;
    }

    public void setUserMessage(String userMessage) {
        this.userMessage = userMessage;
    }

    public List<ChatHistoryItem> getChatHistory() {
        return chatHistory;
    }

    public void setChatHistory(List<ChatHistoryItem> chatHistory) {
        this.chatHistory = chatHistory;
    }
}
