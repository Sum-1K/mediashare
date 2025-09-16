package com.example.demo.model;

import java.io.Serializable;
import java.time.LocalDateTime;

import org.hibernate.annotations.CreationTimestamp;

import jakarta.persistence.Embeddable;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.MapsId;
import jakarta.persistence.Table;

@Entity
@Table(name = "follows")
public class Follow {

    // Composite Key Class
    @Embeddable
    public static class FollowId implements Serializable {
        private Long follower_id;
        private Long followee_id;

        public FollowId() {}

        public FollowId(Long follower_id, Long followee_id) {
            this.follower_id = follower_id;
            this.followee_id = followee_id;
        }

        // Getters and Setters
        public Long getFollower_id() { return follower_id; }
        public void setFollower_id(Long follower_id) { this.follower_id = follower_id; }

        public Long getFollowee_id() { return followee_id; }
        public void setFollowee_id(Long followee_id) { this.followee_id = followee_id; }

        // hashCode and equals required for composite key
        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (!(o instanceof FollowId)) return false;
            FollowId that = (FollowId) o;
            return follower_id.equals(that.follower_id) && followee_id.equals(that.followee_id);
        }

        @Override
        public int hashCode() {
            return follower_id.hashCode() + followee_id.hashCode();
        }
    }

    @EmbeddedId
    private FollowId id;

    // Many-to-One relationships to User
    @MapsId("follower_id")
    @ManyToOne
    @JoinColumn(name = "follower_id", nullable = false)
    private User follower;

    @MapsId("followee_id")
    @ManyToOne
    @JoinColumn(name = "followee_id", nullable = false)
    private User followee;

    private boolean is_close_friend;

    @CreationTimestamp
    private LocalDateTime since;

    // Constructors
    public Follow() {}

    public Follow(User follower, User followee, boolean is_close_friend) {
        this.follower = follower;
        this.followee = followee;
        this.is_close_friend = is_close_friend;
        this.id = new FollowId(follower.getUser_id(), followee.getUser_id());
    }

    // Getters and Setters
    public FollowId getId() { return id; }
    public void setId(FollowId id) { this.id = id; }

    public User getFollower() { return follower; }
    public void setFollower(User follower) { this.follower = follower; }

    public User getFollowee() { return followee; }
    public void setFollowee(User followee) { this.followee = followee; }

    public boolean isIs_close_friend() { return is_close_friend; }
    public void setIs_close_friend(boolean is_close_friend) { this.is_close_friend = is_close_friend; }

    public LocalDateTime getSince() { return since; }
    public void setSince(LocalDateTime since) { this.since = since; }
}
