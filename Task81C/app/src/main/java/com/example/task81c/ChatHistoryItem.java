package com.example.task81c;

public class ChatHistoryItem {
    private String User;
    private String Llama;

    public ChatHistoryItem(String user, String llama) {
        User = user;
        Llama = llama;
    }

    public String getUser() {
        return User;
    }

    public void setUser(String user) {
        User = user;
    }

    public String getLlama() {
        return Llama;
    }

    public void setLlama(String llama) {
        Llama = llama;
    }
}
