package com.example.demo.model;

import jakarta.persistence.*;

@Entity
@Table(name = "tag")   // Table name in your database
public class Tag {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long tag_id;

    // Foreign key to users table
    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false) 
    private User user;

    // Foreign key to content table
    @ManyToOne
    @JoinColumn(name = "content_id", nullable = false)
    private Content content;

    // ---------- Getters and Setters ----------
    public Long getTag_id() { return tag_id;}
    public void setTag_id(Long tag_id) { this.tag_id = tag_id;}

    public User getUser() { return user;}
    public void setUser(User user) { this.user = user;}

    public Content getContent() { return content;}
    public void setContent(Content content) { this.content = content;}
}
