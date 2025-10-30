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
    
    @GetMapping
    public String getNotifications(HttpSession session, Model model) {
        User user = (User) session.getAttribute("user");
        if (user == null) {
            return "redirect:/login";
        }
        
        List<Notification> notifications = notificationService.getUserNotifications(user.getUser_id());
        
        // Enhanced: Add tagger username and other data for tag notifications
        List<Map<String, Object>> enhancedNotifications = new ArrayList<>();
        
        for (Notification notification : notifications) {
            Map<String, Object> enhancedNotif = new HashMap<>();
            enhancedNotif.put("notification", notification);
            
            // Add additional data for tag notifications
            if (notification.getTagId() != null) {
                Tag tag = tagDao.findById(notification.getTagId());
                if (tag != null) {
                    // Get the tagger user (the one who created the original content)
                    Content originalContent = contentDao.findById(tag.getContent_id());
                    //public Long findOwnerIdByContentId(Long contentId) 
                    
                    if (originalContent != null) {
                        User taggerUser = userDao.findById(originalContent.getUserId());
                        enhancedNotif.put("taggerUsername", taggerUser != null ? taggerUser.getUser_name() : "Unknown User");
                        
                        // Add tag status for display
                        enhancedNotif.put("tagStatus", tag.getStatus());
                    }
                }
            }
            
            enhancedNotifications.add(enhancedNotif);
        }        
                
        model.addAttribute("enhancedNotifications", enhancedNotifications);
        model.addAttribute("notifications", notifications); // Add both for backward compatibility
                
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
                                   @RequestParam String action, // "accept" or "reject"
                                   HttpSession session) {
        
        User user = (User) session.getAttribute("user"); // Changed from "loggedInUser" to "user"
        if (user == null) {
            return "redirect:/login";
        }

        try {
            Notification notification = notificationDao.findById(notificationId);
            
            // Security check - ensure the notification belongs to the logged-in user
            if (notification == null || !notification.getUserId().equals(user.getUser_id())) {
                return "redirect:/notifications?error=unauthorized";
            }
            
            // Check if notification is already read/processed
            if (notification.getIsRead()) {
                return "redirect:/notifications?error=already_processed";
            }

            Tag tag = tagDao.findById(notification.getTagId());
            if (tag == null) {
                return "redirect:/notifications?error=tag_not_found";
            }
            
            if ("accept".equalsIgnoreCase(action)) {
                // Update tag status to ACCEPTED
                tag.setStatus("ACCEPTED");
                //tagDao.update(tag);
                tagDao.updateStatus(tag.getTag_id(), tag.getStatus());

                
                // Create a new post for the tagged user (without tags/hashtags)
                createRepostForTaggedUser(user, tag.getContent_id());
                
            } else if ("reject".equalsIgnoreCase(action)) {
                // Update tag status to DECLINED
                tag.setStatus("DECLINED");
                //tagDao.update(tag);
                tagDao.updateStatus(tag.getTag_id(), tag.getStatus());

            }
            
            // Mark notification as read
            notification.setIsRead(true);
            notificationDao.update(notification);
            System.out.println("✅ Updated notification " + notificationId + " is_read to true");
            System.out.println("✅ Updated tag " + tag.getTag_id() + " status to " + tag.getStatus());
            
            return "redirect:/notifications?success=true";
            
        } catch (Exception e) {
            e.printStackTrace();
            return "redirect:/notifications?error=processing_failed";
        }
    }

    private void createRepostForTaggedUser(User user, Long originalContentId) throws IOException {
        // Get the original content
        Content originalContent = contentDao.findById(originalContentId);
        if (originalContent == null) {
            throw new IOException("Original content not found with ID: " + originalContentId);
        }
        
        Post originalPost = postDao.findByContentId(originalContentId);
        if (originalPost == null) {
            throw new IOException("Original post not found for content ID: " + originalContentId);
        }
        
        // Get original media files
        List<Media> originalMedia = mediaDao.findByPostId(originalContentId);
        
        // 1. Insert into content table for the tagged user
        Content newContent = new Content();
        newContent.setUserId(user.getUser_id());
        newContent.setCreatedAt(LocalDateTime.now());
        Long newContentId = contentDao.saveAndReturnId(newContent);
        
        // 2. Insert into post table with same caption but don't process hashtags
        Post newPost = new Post();
        newPost.setPostId(newContentId);
        newPost.setCaption(originalPost.getCaption()); // Keep caption but don't process hashtags
        postDao.save(newPost);
        
        // 3. Copy media files to new location and create new media entries
        String uploadDir = "src/main/resources/static/uploads/";
        int order = 1;
        
        for (Media originalMediaItem : originalMedia) {
            // Read the original file
            Path originalPath = Paths.get(originalMediaItem.getUrl());
            if (!Files.exists(originalPath)) {
                System.err.println("Original media file not found: " + originalMediaItem.getUrl());
                continue;
            }
            
            String originalFilename = originalPath.getFileName().toString();
            
            // Create new filename to avoid conflicts
            String newFilename = user.getUser_id() + "_repost_" + System.currentTimeMillis() + "_" + originalFilename;
            Path newPath = Paths.get(uploadDir, newFilename);
            
            // Ensure upload directory exists
            Files.createDirectories(newPath.getParent());
            
            // Copy the file
            Files.copy(originalPath, newPath, StandardCopyOption.REPLACE_EXISTING);
            
            // Insert into media table
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