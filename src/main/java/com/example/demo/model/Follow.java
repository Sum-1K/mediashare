package com.example.demo.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "follows")
public class Follow {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "follower_id")
    private Long followerId;
    
    @Column(name = "followee_id")
    private Long followeeId;
    
    @Column(name = "is_close_friend")
    private boolean isCloseFriend;
    
    private LocalDateTime since;

    // ... getters and setters
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