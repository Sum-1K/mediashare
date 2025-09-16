package com.example.demo.model;

import java.time.LocalDateTime;

import org.hibernate.annotations.CreationTimestamp;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.Lob;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "comment")
public class Comment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long comment_id;

    @Lob
    private String content;

    @CreationTimestamp
    private LocalDateTime created_at;

    // Many comments belong to one user
    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    // Many comments belong to one content
    @ManyToOne
    @JoinColumn(name = "content_id", nullable = false)
    private Content contentEntity;

    // Getters and Setters
    public Long getComment_id() { return comment_id; }
    public void setComment_id(Long comment_id) { this.comment_id = comment_id; }

    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }

    public LocalDateTime getCreated_at() { return created_at; }
    public void setCreated_at(LocalDateTime created_at) { this.created_at = created_at; }

    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }

    public Content getContentEntity() { return contentEntity; }
    public void setContentEntity(Content contentEntity) { this.contentEntity = contentEntity; }
}
