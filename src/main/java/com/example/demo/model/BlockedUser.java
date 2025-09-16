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
@Table(name = "blocked_users")
public class BlockedUser {

    // Composite Key Class
    @Embeddable
    public static class BlockedUserId implements Serializable {
        private Long blocker_by_id;
        private Long blocked_to_id;

        public BlockedUserId() {}

        public BlockedUserId(Long blocker_by_id, Long blocked_to_id) {
            this.blocker_by_id = blocker_by_id;
            this.blocked_to_id = blocked_to_id;
        }

        // Getters and Setters
        public Long getBlocker_by_id() { return blocker_by_id; }
        public void setBlocker_by_id(Long blocker_by_id) { this.blocker_by_id = blocker_by_id; }

        public Long getBlocked_to_id() { return blocked_to_id; }
        public void setBlocked_to_id(Long blocked_to_id) { this.blocked_to_id = blocked_to_id; }

        // hashCode and equals for composite key
        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (!(o instanceof BlockedUserId)) return false;
            BlockedUserId that = (BlockedUserId) o;
            return blocker_by_id.equals(that.blocker_by_id) && blocked_to_id.equals(that.blocked_to_id);
        }

        @Override
        public int hashCode() {
            return blocker_by_id.hashCode() + blocked_to_id.hashCode();
        }
    }

    @EmbeddedId
    private BlockedUserId id;

    // Many-to-One relationships to User
    @MapsId("blocker_by_id")
    @ManyToOne
    @JoinColumn(name = "blocker_by_id", nullable = false)
    private User blocker;

    @MapsId("blocked_to_id")
    @ManyToOne
    @JoinColumn(name = "blocked_to_id", nullable = false)
    private User blocked;

    @CreationTimestamp
    private LocalDateTime since;

    // Constructors
    public BlockedUser() {}

    public BlockedUser(User blocker, User blocked) {
        this.blocker = blocker;
        this.blocked = blocked;
        this.id = new BlockedUserId(blocker.getUser_id(), blocked.getUser_id());
    }

    // Getters and Setters
    public BlockedUserId getId() { return id; }
    public void setId(BlockedUserId id) { this.id = id; }

    public User getBlocker() { return blocker; }
    public void setBlocker(User blocker) { this.blocker = blocker; }

    public User getBlocked() { return blocked; }
    public void setBlocked(User blocked) { this.blocked = blocked; }

    public LocalDateTime getSince() { return since; }
    public void setSince(LocalDateTime since) { this.since = since; }
}
