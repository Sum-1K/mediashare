package com.example.demo.model;

import java.time.LocalDateTime;

public class Comment {
    private Long commentId;
    private String content;
    private LocalDateTime createdAt;
    private Long userId;
    private Long contentId;

    public Comment() {}

    public Comment(Long commentId, String content, LocalDateTime createdAt, Long userId, Long contentId) {
        this.commentId = commentId;
        this.content = content;
        this.createdAt = createdAt;
        this.userId = userId;
        this.contentId = contentId;
    }

    // Getters and setters
    public Long getCommentId() { return commentId; }
    public void setCommentId(Long commentId) { this.commentId = commentId; }

    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }

    public Long getContentId() { return contentId; }
    public void setContentId(Long contentId) { this.contentId = contentId; }
}