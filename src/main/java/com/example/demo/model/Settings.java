package com.example.demo.model;

import java.time.LocalDateTime;

public class Settings {
    private Long settingId;
    private Long userId;
    private String theme;
    private Boolean storyArchivedEnable;
    private Boolean allowTags;
    private Boolean notificationTags;
    private LocalDateTime createdAt;

    public Settings() {}

    public Settings(Long settingId, Long userId, String theme, Boolean storyArchivedEnable,
                    Boolean allowTags, Boolean notificationTags, LocalDateTime createdAt) {
        this.settingId = settingId;
        this.userId = userId;
        this.theme = theme;
        this.storyArchivedEnable = storyArchivedEnable;
        this.allowTags = allowTags;
        this.notificationTags = notificationTags;
        this.createdAt = createdAt;
    }

    // Getters and setters
    public Long getSettingId() { return settingId; }
    public void setSettingId(Long settingId) { this.settingId = settingId; }

    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }

    public String getTheme() { return theme; }
    public void setTheme(String theme) { this.theme = theme; }

    public Boolean getStoryArchivedEnable() { return storyArchivedEnable; }
    public void setStoryArchivedEnable(Boolean storyArchivedEnable) { this.storyArchivedEnable = storyArchivedEnable; }

    public Boolean getAllowTags() { return allowTags; }
    public void setAllowTags(Boolean allowTags) { this.allowTags = allowTags; }

    public Boolean getNotificationTags() { return notificationTags; }
    public void setNotificationTags(Boolean notificationTags) { this.notificationTags = notificationTags; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}