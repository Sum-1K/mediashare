package com.example.demo.model;

import java.time.LocalDateTime;

import org.hibernate.annotations.CreationTimestamp;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.Lob;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "notification")
public class Notification {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long notif_id;

    @CreationTimestamp
    private LocalDateTime created_at;

    @Lob
    private String notificationContent;

    // Many notifications can belong to one user
    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    // One-to-one relationships
    @OneToOne
    @JoinColumn(name = "chat_id", unique = true)
    private Chat chat;

    @OneToOne
    @JoinColumn(name = "request_id", unique = true)
    private FollowRequest followRequest;

    @OneToOne
    @JoinColumn(name = "comment_id", unique = true)
    private Comment comment;

    @OneToOne
    @JoinColumn(name = "tag_id", unique = true)
    private Tag tag;

    @OneToOne
    @JoinColumn(name = "like_id", unique = true)
    private Like like;

    // Getters and Setters
    public Long getNotif_id() { return notif_id; }
    public void setNotif_id(Long notif_id) { this.notif_id = notif_id; }

    public LocalDateTime getCreated_at() { return created_at; }
    public void setCreated_at(LocalDateTime created_at) { this.created_at = created_at; }

    public String getNotificationContent() { return notificationContent; }
    public void setNotificationContent(String notificationContent) { this.notificationContent = notificationContent; }

    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }

    public Chat getChat() { return chat; }
    public void setChat(Chat chat) { this.chat = chat; }

    public FollowRequest getFollowRequest() { return followRequest; }
    public void setFollowRequest(FollowRequest followRequest) { this.followRequest = followRequest; }

    public Comment getComment() { return comment; }
    public void setComment(Comment comment) { this.comment = comment; }

    public Tag getTag() { return tag; }
    public void setTag(Tag tag) { this.tag = tag; }

    public Like getLike() { return like; }
    public void setLike(Like like) { this.like = like; }
}
