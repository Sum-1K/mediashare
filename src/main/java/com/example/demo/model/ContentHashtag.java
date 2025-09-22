package com.example.demo.model;

public class ContentHashtag {
    private Long content_id;
    private Long hashtag_id;

    // Constructors
    public ContentHashtag() {}

    public ContentHashtag(Long content_id, Long hashtag_id) {
        this.content_id = content_id;
        this.hashtag_id = hashtag_id;
    }

    // Getters & Setters
    public Long getContent_id() { return content_id; }
    public void setContent_id(Long content_id) { this.content_id = content_id; }

    public Long getHashtag_id() { return hashtag_id; }
    public void setHashtag_id(Long hashtag_id) { this.hashtag_id = hashtag_id; }
}