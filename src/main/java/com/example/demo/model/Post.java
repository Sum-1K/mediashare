package com.example.demo.model;

import jakarta.persistence.*;

@Entity
@Table(name = "posts") 
public class Post {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long post_id;

    @Column(length = 500)  // optional: set max length for caption
    private String caption;

    // Getters and Setters
    public Long getPost_id() { return post_id; }
    public void setPost_id(Long post_id) { this.post_id = post_id; }

    public String getCaption() { return caption; }
    public void setCaption(String caption) { this.caption = caption; }
}
