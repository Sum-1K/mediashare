package com.example.demo.model;

import java.time.LocalDateTime;

public class Content {
    private Long contentId;
    private LocalDateTime createdAt;
    private Long userId; // foreign key reference

    // Getters and setters
    public Long getContentId() { return contentId; }
    public void setContentId(Long contentId) { this.contentId = contentId; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }
}
