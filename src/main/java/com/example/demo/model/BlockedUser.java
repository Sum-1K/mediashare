
package com.example.demo.model;

import java.time.LocalDateTime;

public class BlockedUser {
    private Long blockerById;
    private Long blockedToId;
    private LocalDateTime since;

    // Constructors
    public BlockedUser() {}

    public BlockedUser(Long blockerById, Long blockedToId, LocalDateTime since) {
        this.blockerById = blockerById;
        this.blockedToId = blockedToId;
        this.since = since;
    }

    // Getters and Setters
    public Long getBlockerById() {
        return blockerById;
    }

    public void setBlockerById(Long blockerById) {
        this.blockerById = blockerById;
    }

    public Long getBlockedToId() {
        return blockedToId;
    }

    public void setBlockedToId(Long blockedToId) {
        this.blockedToId = blockedToId;
    }

    public LocalDateTime getSince() {
        return since;
    }

    public void setSince(LocalDateTime since) {
        this.since = since;
    }
}