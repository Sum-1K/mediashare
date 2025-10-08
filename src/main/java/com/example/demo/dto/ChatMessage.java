package com.example.demo.dto;
import java.time.LocalDateTime;

public class ChatMessage {
    private Long senderId;
    private Long receiverId;
    private String content;

    public ChatMessage() {}

    public ChatMessage(Long sender, Long receiver, String content, LocalDateTime timestamp) {
        this.senderId = sender;
        this.receiverId = receiver;
        this.content = content;
    }

    // getters and setters
    public Long getSenderId() { return senderId; }
    public void setSenderId(Long sender) { this.senderId = sender; }

    public Long getReceiverId() { return receiverId; }
    public void setReceiverId(Long receiver) { this.receiverId = receiver; }

    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }
}
