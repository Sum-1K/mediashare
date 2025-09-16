package com.example.demo.model;

import java.io.Serializable;

import jakarta.persistence.Embeddable;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.MapsId;
import jakarta.persistence.Table;

@Entity
@Table(name = "saved_post")
public class SavedPost {

    // Composite Key Class
    @Embeddable
    public static class SavedPostId implements Serializable {
        private Long user_id;
        private Long saved_post_id;

        public SavedPostId() {}

        public SavedPostId(Long user_id, Long saved_post_id) {
            this.user_id = user_id;
            this.saved_post_id = saved_post_id;
        }

        // Getters and Setters
        public Long getUser_id() { return user_id; }
        public void setUser_id(Long user_id) { this.user_id = user_id; }

        public Long getSaved_post_id() { return saved_post_id; }
        public void setSaved_post_id(Long saved_post_id) { this.saved_post_id = saved_post_id; }

        // hashCode and equals for composite key
        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (!(o instanceof SavedPostId)) return false;
            SavedPostId that = (SavedPostId) o;
            return user_id.equals(that.user_id) && saved_post_id.equals(that.saved_post_id);
        }

        @Override
        public int hashCode() {
            return user_id.hashCode() + saved_post_id.hashCode();
        }
    }

    @EmbeddedId
    private SavedPostId id;

    // Many-to-One relationships
    @MapsId("user_id")
    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @MapsId("saved_post_id")
    @ManyToOne
    @JoinColumn(name = "saved_post_id", nullable = false)
    private Post post;

    // Constructors
    public SavedPost() {}

    public SavedPost(User user, Post post) {
        this.user = user;
        this.post = post;
        this.id = new SavedPostId(user.getUser_id(), post.getPost_id());
    }

    // Getters and Setters
    public SavedPostId getId() { return id; }
    public void setId(SavedPostId id) { this.id = id; }

    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }

    public Post getPost() { return post; }
    public void setPost(Post post) { this.post = post; }
}
