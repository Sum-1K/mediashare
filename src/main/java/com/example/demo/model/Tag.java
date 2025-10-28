package com.example.demo.model;

import java.time.LocalDateTime;

public class Tag {
    private Long tag_id;
    private Long user_id;
    private Long content_id;
    private String status;    // PENDING, ACCEPTED, DECLINED
    private LocalDateTime created_at;

    // Constructors
    public Tag() {}

    public Tag(Long tag_id, Long user_id, Long content_id, String status, LocalDateTime created_at) {
        this.tag_id = tag_id;
        this.user_id = user_id;
        this.content_id = content_id;
        this.status = status;
        this.created_at=created_at;
    }

    // Getters & Setters
    public Long getTag_id() { return tag_id;}
    public void setTag_id(Long tag_id) { this.tag_id = tag_id;}

    public Long getUser_id() { return user_id;}
    public void setUser_id(Long user_id) { this.user_id = user_id;}

    public Long getContent_id() { return content_id;}
    public void setContent_id(Long content_id) { this.content_id = content_id;}

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public LocalDateTime getCreatedAt() { return created_at; }
    public void setCreatedAt( LocalDateTime createdAt) { this.created_at = createdAt;}
}