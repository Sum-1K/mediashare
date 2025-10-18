package com.example.demo.model;

import java.time.LocalDateTime;

public class Chat {
    private Long chat_id;
    private Boolean seen;
    private String message;
    private LocalDateTime sent_at;
    private Long sender_id;
    private Long receiver_id;
    private Long replied_to_id;

    // Getters and Setters
    public Long getChat_id() { return chat_id; }
    public void setChat_id(Long chat_id) { this.chat_id = chat_id; }

    public Boolean getSeen() { return seen; }
    public void setSeen(Boolean seen) { this.seen = seen; }

    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }

    public LocalDateTime getSent_at() { return sent_at; }
    public void setSent_at(LocalDateTime sent_at) { this.sent_at = sent_at; }

    public Long getSender_id() { return sender_id; }
    public void setSender_id(Long sender_id) { this.sender_id = sender_id; }

    public Long getReceiver_id() { return receiver_id; }
    public void setReceiver_id(Long receiver_id) { this.receiver_id = receiver_id; }

    public Long getReplied_to_id() { return replied_to_id; }
    public void setReplied_to_id(Long replied_to_id) { this.replied_to_id = replied_to_id; }

}