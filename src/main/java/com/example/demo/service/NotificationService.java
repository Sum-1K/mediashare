// NotificationService.java
package com.example.demo.service;

import com.example.demo.dao.NotificationDao;
import com.example.demo.dao.UserDao;
import com.example.demo.model.Notification;
import com.example.demo.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class NotificationService {
    
    @Autowired
    private NotificationDao notificationDao;
    
    @Autowired
    private UserDao userDao;
    
    // Create notification for likes
    public void createLikeNotification(Long likerUserId, Long contentOwnerId, Long contentId, Long likeId) {
        User liker = userDao.findById(likerUserId);
        if (liker == null || likerUserId.equals(contentOwnerId)) return; // No self-notification
        
        String content = liker.getUser_name() + " liked your post";
        Notification notification = new Notification();
        notification.setCreatedAt(LocalDateTime.now());
        notification.setNotificationContent(content);
        notification.setUserId(contentOwnerId); // Notify the content owner
        notification.setLikeId(likeId);
        
        notificationDao.insert(notification);
    }
    
    // Create notification for comments
    public void createCommentNotification(Long commenterUserId, Long contentOwnerId, Long contentId, Long commentId) {
        User commenter = userDao.findById(commenterUserId);
        if (commenter == null || commenterUserId.equals(contentOwnerId)) return;
        
        String content = commenter.getUser_name() + " commented on your post";
        Notification notification = new Notification();
        notification.setCreatedAt(LocalDateTime.now());
        notification.setNotificationContent(content);
        notification.setUserId(contentOwnerId);
        notification.setCommentId(commentId);
        
        notificationDao.insert(notification);
    }
    
    // Create notification for follow requests
    public void createFollowRequestNotification(Long senderUserId, Long receiverUserId, Long followRequestId) {
        User sender = userDao.findById(senderUserId);
        if (sender == null) return;
        
        String content = sender.getUser_name() + " sent you a follow request";
        Notification notification = new Notification();
        notification.setCreatedAt(LocalDateTime.now());
        notification.setNotificationContent(content);
        notification.setUserId(receiverUserId);
        notification.setFollowRequestId(followRequestId);
        
        notificationDao.insert(notification);
    }
    
    // Create notification for accepted follow requests
    public void createFollowAcceptedNotification(Long accepterUserId, Long followerUserId) {
        User accepter = userDao.findById(accepterUserId);
        if (accepter == null) return;
        
        String content = accepter.getUser_name() + " accepted your follow request";
        Notification notification = new Notification();
        notification.setCreatedAt(LocalDateTime.now());
        notification.setNotificationContent(content);
        notification.setUserId(followerUserId);
        
        notificationDao.insert(notification);
    }
    
    // Create notification for new messages
    public void createMessageNotification(Long senderUserId, Long receiverUserId, Long chatId) {
        User sender = userDao.findById(senderUserId);
        if (sender == null) return;
        
        String content = sender.getUser_name() + " sent you a message";
        Notification notification = new Notification();
        notification.setCreatedAt(LocalDateTime.now());
        notification.setNotificationContent(content);
        notification.setUserId(receiverUserId);
        notification.setChatId(chatId);
        
        notificationDao.insert(notification);
    }
    
    // Get notifications for a user
    public List<Notification> getUserNotifications(Long userId) {
        return notificationDao.findByUserId(userId);
    }
    
    // Mark notification as read (optional enhancement)
    public void markAsRead(Long notificationId) {
        // You can add a 'read' boolean field to Notification model if needed
    }

    // Add this method to your NotificationService.java
    public void createReplyNotification(Long senderUserId, Long receiverUserId, Long chatId, Long repliedToMessageId) {
        User sender = userDao.findById(senderUserId);
        if (sender == null || senderUserId.equals(receiverUserId)) return;
        
        String content = sender.getUser_name() + " replied to your message";
        Notification notification = new Notification();
        notification.setCreatedAt(LocalDateTime.now());
        notification.setNotificationContent(content);
        notification.setUserId(receiverUserId);
        notification.setChatId(chatId);
        
        notificationDao.insert(notification);
    }

    public void createTagNotification(Long taggerUserId, Long taggedUserId, Long contentId, Long tagId) {
        User tagger = userDao.findById(taggerUserId);
        if (tagger == null || taggerUserId.equals(taggedUserId)) return; // No self-tagging notification
        
        String content = tagger.getUser_name() + " tagged you in a post";
        Notification notification = new Notification();
        notification.setCreatedAt(LocalDateTime.now());
        notification.setNotificationContent(content);
        notification.setUserId(taggedUserId); // Notify the tagged user
        notification.setTagId(tagId);
        //notification.setContentId(contentId); // Store contentId for navigation
        
        notificationDao.insert(notification);
    }
}