package com.example.demo.model;

import java.time.LocalDateTime;

import org.hibernate.annotations.CreationTimestamp;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "settings")
public class Settings {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long setting_id;

    @OneToOne
    @JoinColumn(name = "user_id", nullable = false, unique = true)
    private User user;

    private String theme;

    private Boolean story_archived_enable;

    private Boolean allow_tags;

    private Boolean notification_tags;

    @CreationTimestamp
    private LocalDateTime created_at;

    // Getters and Setters
    public Long getSetting_id() { return setting_id; }
    public void setSetting_id(Long setting_id) { this.setting_id = setting_id; }

    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }

    public String getTheme() { return theme; }
    public void setTheme(String theme) { this.theme = theme; }

    public Boolean getStory_archived_enable() { return story_archived_enable; }
    public void setStory_archived_enable(Boolean story_archived_enable) { this.story_archived_enable = story_archived_enable; }

    public Boolean getAllow_tags() { return allow_tags; }
    public void setAllow_tags(Boolean allow_tags) { this.allow_tags = allow_tags; }

    public Boolean getNotification_tags() { return notification_tags; }
    public void setNotification_tags(Boolean notification_tags) { this.notification_tags = notification_tags; }

    public LocalDateTime getCreated_at() { return created_at; }
    public void setCreated_at(LocalDateTime created_at) { this.created_at = created_at; }
}
