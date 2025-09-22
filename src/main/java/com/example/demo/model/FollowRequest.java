package com.example.demo.model;

import java.time.LocalDateTime;

public class FollowRequest {
    private Long request_id;
    private Long sender_id;
    private Long receiver_id;
    private String status;
    private LocalDateTime created_at;

    // Getters and Setters
    public Long getRequest_id() { return request_id; }
    public void setRequest_id(Long request_id) { this.request_id = request_id; }

    public Long getSender_id() { return sender_id; }
    public void setSender_id(Long sender_id) { this.sender_id = sender_id; }

    public Long getReceiver_id() { return receiver_id; }
    public void setReceiver_id(Long receiver_id) { this.receiver_id = receiver_id; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public LocalDateTime getCreated_at() { return created_at; }
    public void setCreated_at(LocalDateTime created_at) { this.created_at = created_at; }
}