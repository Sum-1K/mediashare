package com.example.demo.model;

import jakarta.persistence.*;

@Entity
@Table(name = "views")
@IdClass(ViewsId.class)   // Tells JPA we’re using composite key
public class Views {

    @Id
    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Id
    @ManyToOne
    @JoinColumn(name = "content_id", nullable = false)
    private Content content;

    // -------- Getters and Setters --------
    public User getUser() { return user;}
    public void setUser(User user) { this.user = user;}

    public Content getContent() { return content;}
    public void setContent(Content content) { this.content = content;}
}
