package com.example.demo.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "follow_requests")
public class FollowRequest {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "request_id")
    private Long request_id;
    
    @Column(name = "sender_id", nullable = false)
    private Long sender_id;
    
    @Column(name = "receiver_id", nullable = false)
    private Long receiver_id;
    
    @Column(name = "status", length = 20)
    private String status = "SENT"; // Changed from PENDING to SENT
    
    @Column(name = "created_at")
    private LocalDateTime created_at;
    
    @Column(name = "sent_at")
    private LocalDateTime sent_at; // New column
    
    // Constructors
    public FollowRequest() {}
    
    public FollowRequest(Long sender_id, Long receiver_id, String status, LocalDateTime created_at) {
        this.sender_id = sender_id;
        this.receiver_id = receiver_id;
        this.status = status;
        this.created_at = created_at;
        this.sent_at = created_at; // Set sent_at to same as created_at
    }
    
    // Getters and Setters
    public Long getRequest_id() { 
        return request_id; 
    }
    
    public void setRequest_id(Long request_id) { 
        this.request_id = request_id; 
    }
    
    public Long getSender_id() { 
        return sender_id; 
    }
    
    public void setSender_id(Long sender_id) { 
        this.sender_id = sender_id; 
    }
    
    public Long getReceiver_id() { 
        return receiver_id; 
    }
    
    public void setReceiver_id(Long receiver_id) { 
        this.receiver_id = receiver_id; 
    }
    
    public String getStatus() { 
        return status; 
    }
    
    public void setStatus(String status) { 
        this.status = status; 
    }
    
    public LocalDateTime getCreated_at() { 
        return created_at; 
    }
    
    public void setCreated_at(LocalDateTime created_at) { 
        this.created_at = created_at; 
    }
    
    public LocalDateTime getSent_at() {
        return sent_at;
    }
    
    public void setSent_at(LocalDateTime sent_at) {
        this.sent_at = sent_at;
    }
    
    // Updated helper methods for new status values
    public boolean isPending() {
        return "SENT".equals(status); // Changed from PENDING to SENT
    }
    
    public boolean isAccepted() {
        return "ACCEPTED".equals(status);
    }
    
    public boolean isRejected() {
        return "DECLINED".equals(status); // Changed from REJECTED to DECLINED
    }
    
    @Override
    public String toString() {
        return "FollowRequest{" +
                "request_id=" + request_id +
                ", sender_id=" + sender_id +
                ", receiver_id=" + receiver_id +
                ", status='" + status + '\'' +
                ", created_at=" + created_at +
                ", sent_at=" + sent_at +
                '}';
    }
}