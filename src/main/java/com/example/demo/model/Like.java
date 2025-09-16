package com.example.demo.model;

import java.time.LocalDateTime;

import org.hibernate.annotations.CreationTimestamp;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "like_table") // "like" is a reserved keyword in SQL, so using "like_table"
public class Like {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long like_id;

    @CreationTimestamp
    private LocalDateTime created_at;

    // Many likes belong to one user
    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    // Many likes belong to one content
    @ManyToOne
    @JoinColumn(name = "content_id", nullable = false)
    private Content contentEntity;

    // Getters and Setters
    public Long getLike_id() { return like_id; }
    public void setLike_id(Long like_id) { this.like_id = like_id; }

    public LocalDateTime getCreated_at() { return created_at; }
    public void setCreated_at(LocalDateTime created_at) { this.created_at = created_at; }

    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }

    public Content getContentEntity() { return contentEntity; }
    public void setContentEntity(Content contentEntity) { this.contentEntity = contentEntity; }
}
