package com.example.demo.model;

public class Hashtag {
    private Long hashtagId;
    private String text;

    public Hashtag() {}

    public Hashtag(Long hashtagId, String text) {
        this.hashtagId = hashtagId;
        this.text = text;
    }

    // Getters and setters
    public Long getHashtagId() { return hashtagId; }
    public void setHashtagId(Long hashtagId) { this.hashtagId = hashtagId; }

    public String getText() { return text; }
    public void setText(String text) { this.text = text; }
}