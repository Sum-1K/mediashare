package com.example.demo.model;

public class Tag {
    private Long tag_id;
    private Long user_id;
    private Long content_id;

    // Constructors
    public Tag() {}

    public Tag(Long tag_id, Long user_id, Long content_id) {
        this.tag_id = tag_id;
        this.user_id = user_id;
        this.content_id = content_id;
    }

    // Getters & Setters
    public Long getTag_id() { return tag_id;}
    public void setTag_id(Long tag_id) { this.tag_id = tag_id;}

    public Long getUser_id() { return user_id;}
    public void setUser_id(Long user_id) { this.user_id = user_id;}

    public Long getContent_id() { return content_id;}
    public void setContent_id(Long content_id) { this.content_id = content_id;}
}