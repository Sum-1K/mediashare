package com.example.demo.model;

import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "chats")
public class Chat {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long chat_id;

    @Column(nullable = false)
    private Boolean seen = false;

    @Lob
    @Column(nullable = false)
    private String message;

    @CreationTimestamp
    private LocalDateTime sent_at;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "sender_id", nullable = false)
    private User sender;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "receiver_id", nullable = false)
    private User receiver;

    // Getters and Setters
    public Long getChat_id() { return chat_id; }
    public void setChat_id(Long chat_id) { this.chat_id = chat_id; }

    public Boolean getSeen() { return seen; }
    public void setSeen(Boolean seen) { this.seen = seen; }

    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }

    public LocalDateTime getSent_at() { return sent_at; }
    public void setSent_at(LocalDateTime sent_at) { this.sent_at = sent_at; }

    public User getSender() { return sender; }
    public void setSender(User sender) { this.sender = sender; }

    public User getReceiver() { return receiver; }
    public void setReceiver(User receiver) { this.receiver = receiver; }
}
