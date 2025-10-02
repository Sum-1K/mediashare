package com.example.demo.dto;

import com.example.demo.model.FollowRequest;
import com.example.demo.model.User;

public class FollowRequestWithUser {
    private FollowRequest followRequest;
    private User sender;
    
    public FollowRequestWithUser(FollowRequest followRequest, User sender) {
        this.followRequest = followRequest;
        this.sender = sender;
    }
    
    // Getters and setters
    public FollowRequest getFollowRequest() {
        return followRequest;
    }
    
    public void setFollowRequest(FollowRequest followRequest) {
        this.followRequest = followRequest;
    }
    
    public User getSender() {
        return sender;
    }
    
    public void setSender(User sender) {
        this.sender = sender;
    }
}