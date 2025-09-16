package com.example.demo.model;

import jakarta.persistence.*;

@Entity
@Table(name = "hashtags")
public class Hashtag {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long hashtag_id;

    @Column(nullable = false, unique = true)
    private String text;   // The hashtag text like #java

    // Getters and Setters
    public Long getHashtag_id() {
        return hashtag_id;
    }

    public void setHashtag_id(Long hashtag_id) {
        this.hashtag_id = hashtag_id;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }
}
