package com.example.demo.model;

import java.time.LocalDateTime;

public class Like {
    private Long likeId;
    private LocalDateTime createdAt;
    private Long userId;
    private Long contentId;

    public Like() {}

    public Like(Long likeId, LocalDateTime createdAt, Long userId, Long contentId) {
        this.likeId = likeId;
        this.createdAt = createdAt;
        this.userId = userId;
        this.contentId = contentId;
    }

    // Getters and setters
    public Long getLikeId() { return likeId; }
    public void setLikeId(Long likeId) { this.likeId = likeId; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }

    public Long getContentId() { return contentId; }
    public void setContentId(Long contentId) { this.contentId = contentId; }
}