package com.example.demo.dto;
import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonProperty;

public class ChatMessage {
    private Long senderId;
    private Long receiverId;
    private String content;
    private LocalDateTime sentAt;

    @JsonProperty("replied_to_id")
    private Long repliedToId;

    private ChatMediaDTO media;

    public ChatMessage() {}

    public ChatMessage(Long sender, Long receiver, String content, LocalDateTime timestamp, Long repliedToId) {
        this.senderId = sender;
        this.receiverId = receiver;
        this.content = content;
        this.repliedToId = repliedToId;
        this.sentAt = timestamp;
    }

    // getters and setters
    public Long getSenderId() { return senderId; }
    public void setSenderId(Long sender) { this.senderId = sender; }

    public Long getReceiverId() { return receiverId; }
    public void setReceiverId(Long receiver) { this.receiverId = receiver; }

    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }

    public Long getRepliedToId() { return repliedToId; }
    public void setRepliedToId(Long repliedToId) { this.repliedToId = repliedToId; }

    public ChatMediaDTO getMedia() { return media; }
    public void setMedia(ChatMediaDTO media) { this.media = media; }

    public LocalDateTime getSentAt() {return sentAt;}
    public void setSentAt(LocalDateTime sentAt) {this.sentAt=sentAt; }
}
