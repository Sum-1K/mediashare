package com.example.demo.dto;

import java.time.LocalDateTime;

public class CommentDTO {
    private Long commentId;
    private String content;
    private LocalDateTime createdAt;
    private Long contentId;
    private Long userId;
    private String username; // from users table

    public CommentDTO(Long commentId, String content, LocalDateTime createdAt, Long contentId, Long userId, String username) {
        this.commentId = commentId;
        this.content = content;
        this.createdAt = createdAt;
        this.contentId = contentId;
        this.userId = userId;
        this.username = username;
    }

    // Getters
    public Long getCommentId() { return commentId; }
    public String getContent() { return content; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public Long getContentId() { return contentId; }
    public Long getUserId() { return userId; }
    public String getUsername() { return username; }
}

