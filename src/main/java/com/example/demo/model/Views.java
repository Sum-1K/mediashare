package com.example.demo.model;

public class Views {
    private Long user_id;
    private Long content_id;

    // Constructors
    public Views() {}

    public Views(Long user_id, Long content_id) {
        this.user_id = user_id;
        this.content_id = content_id;
    }

    // Getters & Setters
    public Long getUser_id() { return user_id;}
    public void setUser_id(Long user_id) { this.user_id = user_id;}

    public Long getContent_id() { return content_id;}
    public void setContent_id(Long content_id) { this.content_id = content_id;}
}