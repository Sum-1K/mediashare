package com.example.demo.model;

import java.time.LocalDateTime;

public class Follow {
    private Long followerId;
    private Long followeeId;
    private boolean isCloseFriend;
    private LocalDateTime since;

    // Constructor
    public Follow() {}

    public Follow(Long followerId, Long followeeId, boolean isCloseFriend, LocalDateTime since) {
        this.followerId = followerId;
        this.followeeId = followeeId;
        this.isCloseFriend = isCloseFriend;
        this.since = since;
    }

    // Getters and Setters
    public Long getFollowerId() {
        return followerId;
    }

    public void setFollowerId(Long followerId) {
        this.followerId = followerId;
    }

    public Long getFolloweeId() {
        return followeeId;
    }

    public void setFolloweeId(Long followeeId) {
        this.followeeId = followeeId;
    }

    public boolean isCloseFriend() {
        return isCloseFriend;
    }

    public void setCloseFriend(boolean closeFriend) {
        isCloseFriend = closeFriend;
    }

    public LocalDateTime getSince() {
        return since;
    }

    public void setSince(LocalDateTime since) {
        this.since = since;
    }
}