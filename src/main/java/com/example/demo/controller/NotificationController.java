package com.example.demo.controller;

import com.example.demo.model.*;
import com.example.demo.service.NotificationService;
import com.example.demo.dao.*;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/notifications")
public class NotificationController {
    
    @Autowired
    private NotificationService notificationService;
    
    @Autowired
    private NotificationDao notificationDao;
    
    @Autowired
    private TagDao tagDao;
    
    @Autowired
    private UserDao userDao;
    
    @Autowired
    private ContentDao contentDao;
    
    @Autowired
    private PostDao postDao;
    
    @Autowired
    private MediaDao mediaDao;
    
    @Autowired
    private LikeDao likeDao;
    
    @Autowired
    private CommentDao commentDao;
    
    @GetMapping
    public String getNotifications(HttpSession session, Model model) {
        User user = (User) session.getAttribute("user");
        if (user == null) {
            return "redirect:/login";
        }
        
        List<Notification> notifications = notificationService.getUserNotifications(user.getUser_id());
        
        // Enhanced notifications with additional data
        List<Map<String, Object>> enhancedNotifications = new ArrayList<>();
        
        for (Notification notification : notifications) {
            Map<String, Object> enhancedNotif = new HashMap<>();
            enhancedNotif.put("notification", notification);
            
            // Add additional data for different notification types
            if (notification.getTagId() != null) {
                Tag tag = tagDao.findById(notification.getTagId());
                if (tag != null) {
                    Content originalContent = contentDao.findById(tag.getContent_id());
                    if (originalContent != null) {
                        User taggerUser = userDao.findById(originalContent.getUserId());
                        enhancedNotif.put("taggerUsername", taggerUser != null ? taggerUser.getUser_name() : "Unknown User");
                        enhancedNotif.put("tagStatus", tag.getStatus());
                    }
                }
            } 
            // Add data for like notifications
            else if (notification.getLikeId() != null) {
                Like like = likeDao.findById(notification.getLikeId());
                if (like != null) {
                    User likerUser = userDao.findById(like.getUserId());
                    enhancedNotif.put("likerUsername", likerUser != null ? likerUser.getUser_name() : "Unknown User");
                    
                    // Ensure notification content is set
                    if (notification.getNotificationContent() == null || notification.getNotificationContent().isEmpty()) {
                        notification.setNotificationContent(likerUser.getUser_name() + " liked your post");
                    }
                }
            }
            // Add data for comment notifications
            else if (notification.getCommentId() != null) {
                Comment comment = commentDao.findById(notification.getCommentId());
                if (comment != null) {
                    User commenterUser = userDao.findById(comment.getUserId());
                    enhancedNotif.put("commenterUsername", commenterUser != null ? commenterUser.getUser_name() : "Unknown User");
                    
                    // Ensure notification content is set
                    if (notification.getNotificationContent() == null || notification.getNotificationContent().isEmpty()) {
                        notification.setNotificationContent(commenterUser.getUser_name() + " commented on your post");
                    }
                }
            }
            
            enhancedNotifications.add(enhancedNotif);
        }
        
        model.addAttribute("enhancedNotifications", enhancedNotifications);
        model.addAttribute("notifications", notifications);
        
        return "notifications";
    }
    
    @GetMapping("/api")
    @ResponseBody
    public List<Notification> getNotificationsApi(HttpSession session) {
        User user = (User) session.getAttribute("user");
        if (user == null) {
            return List.of();
        }
        
        return notificationService.getUserNotifications(user.getUser_id());
    }

    @PostMapping("/tag/respond")
    public String handleTagResponse(@RequestParam Long notificationId,
                                   @RequestParam String action,
                                   HttpSession session) {
        
        User user = (User) session.getAttribute("user");
        if (user == null) {
            return "redirect:/login";
        }

        try {
            Notification notification = notificationDao.findById(notificationId);
            
            if (notification == null || !notification.getUserId().equals(user.getUser_id())) {
                return "redirect:/notifications?error=unauthorized";
            }
            
            if (notification.getIsRead()) {
                return "redirect:/notifications?error=already_processed";
            }

            Tag tag = tagDao.findById(notification.getTagId());
            if (tag == null) {
                return "redirect:/notifications?error=tag_not_found";
            }
            
            if ("accept".equalsIgnoreCase(action)) {
                tag.setStatus("ACCEPTED");
                tagDao.updateStatus(tag.getTag_id(), tag.getStatus());
                createRepostForTaggedUser(user, tag.getContent_id());
            } else if ("reject".equalsIgnoreCase(action)) {
                tag.setStatus("DECLINED");
                tagDao.updateStatus(tag.getTag_id(), tag.getStatus());
            }
            
            notification.setIsRead(true);
            notificationDao.update(notification);
            
            return "redirect:/notifications?success=true";
            
        } catch (Exception e) {
            e.printStackTrace();
            return "redirect:/notifications?error=processing_failed";
        }
    }

    private void createRepostForTaggedUser(User user, Long originalContentId) throws IOException {
        Content originalContent = contentDao.findById(originalContentId);
        if (originalContent == null) {
            throw new IOException("Original content not found with ID: " + originalContentId);
        }
        
        Post originalPost = postDao.findByContentId(originalContentId);
        if (originalPost == null) {
            throw new IOException("Original post not found for content ID: " + originalContentId);
        }
        
        List<Media> originalMedia = mediaDao.findByPostId(originalContentId);
        
        Content newContent = new Content();
        newContent.setUserId(user.getUser_id());
        newContent.setCreatedAt(LocalDateTime.now());
        Long newContentId = contentDao.saveAndReturnId(newContent);
        
        Post newPost = new Post();
        newPost.setPostId(newContentId);
        newPost.setCaption(originalPost.getCaption());
        postDao.save(newPost);
        
        String uploadDir = "src/main/resources/static/uploads/";
        int order = 1;
        
        for (Media originalMediaItem : originalMedia) {
            Path originalPath = Paths.get(originalMediaItem.getUrl());
            if (!Files.exists(originalPath)) {
                System.err.println("Original media file not found: " + originalMediaItem.getUrl());
                continue;
            }
            
            String originalFilename = originalPath.getFileName().toString();
            String newFilename = user.getUser_id() + "_repost_" + System.currentTimeMillis() + "_" + originalFilename;
            Path newPath = Paths.get(uploadDir, newFilename);
            
            Files.createDirectories(newPath.getParent());
            Files.copy(originalPath, newPath, StandardCopyOption.REPLACE_EXISTING);
            
            Media newMedia = new Media();
            newMedia.setPostId(newContentId);
            newMedia.setUrl("src/main/resources/static/uploads/" + newFilename);
            newMedia.setType(originalMediaItem.getType());
            newMedia.setMediaOrder(order++);
            mediaDao.insert(newMedia);
        }
        
        System.out.println("Created repost for user " + user.getUser_id() + " with content ID: " + newContentId);
    }
}