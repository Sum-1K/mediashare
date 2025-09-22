package com.example.demo.model;

public class SavedPost {
    private Long userId;
    private Long savedPostId;

    // Constructors
    public SavedPost() {}

    public SavedPost(Long userId, Long savedPostId) {
        this.userId = userId;
        this.savedPostId = savedPostId;
    }

    // Getters and Setters
    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public Long getSavedPostId() {
        return savedPostId;
    }

    public void setSavedPostId(Long savedPostId) {
        this.savedPostId = savedPostId;
    }
}