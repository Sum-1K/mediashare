package com.example.demo.model;

import java.time.LocalDateTime;

public class Notification {
    private Long notifId;
    private LocalDateTime createdAt;
    private String notificationContent;
    private Long userId;           // Many-to-one
    private Long chatId;           // One-to-one
    private Long followRequestId;  // One-to-one
    private Long commentId;        // One-to-one
    private Long tagId;            // One-to-one
    private Long likeId;           // One-to-one
    private Boolean is_read;

    // Constructor
    public Notification(Long notifId, LocalDateTime createdAt, String notificationContent,
                        Long userId, Long chatId, Long followRequestId,
                        Long commentId, Long tagId, Long likeId, Boolean is_read) {
        this.notifId = notifId;
        this.createdAt = createdAt;
        this.notificationContent = notificationContent;
        this.userId = userId;
        this.chatId = chatId;
        this.followRequestId = followRequestId;
        this.commentId = commentId;
        this.tagId = tagId;
        this.likeId = likeId;
        this.is_read = is_read;
    }

    // Default constructor
    public Notification() {}

    // Getters and setters
    public Long getNotifId() { return notifId; }
    public void setNotifId(Long notifId) { this.notifId = notifId; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public String getNotificationContent() { return notificationContent; }
    public void setNotificationContent(String notificationContent) { this.notificationContent = notificationContent; }

    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }

    public Long getChatId() { return chatId; }
    public void setChatId(Long chatId) { this.chatId = chatId; }

    public Long getFollowRequestId() { return followRequestId; }
    public void setFollowRequestId(Long followRequestId) { this.followRequestId = followRequestId; }

    public Long getCommentId() { return commentId; }
    public void setCommentId(Long commentId) { this.commentId = commentId; }

    public Long getTagId() { return tagId; }
    public void setTagId(Long tagId) { this.tagId = tagId; }

    public Long getLikeId() { return likeId; }
    public void setLikeId(Long likeId) { this.likeId = likeId; }

    public Boolean getIsRead() { return is_read; }
    public void setIsRead(Boolean is_read) { this.is_read = is_read; }

}