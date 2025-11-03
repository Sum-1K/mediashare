package com.example.demo.controller;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.example.demo.dao.LikeDao;
import com.example.demo.dao.ContentDao;
import com.example.demo.model.Like;
import com.example.demo.model.User;
import com.example.demo.service.NotificationService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.stereotype.Controller;
import java.time.LocalDateTime;
import java.util.List;

@Controller
@RequestMapping("/like")
public class LikeController {

    @Autowired
    private LikeDao likeDao;

    @Autowired
    private NotificationService notificationService;

    @Autowired
    private ContentDao contentDao;

    @PostMapping("/toggle")
    public String toggleLike(
            @RequestParam Long contentId,
            @RequestParam String type,
            HttpSession session) {

        User user = (User) session.getAttribute("user");
        if (user == null) {
            System.out.println("[ERROR] User not logged in!");
            return "redirect:/login";
        }
        Long userId = user.getUser_id();

        // Remove existing like if it exists
        int rowsDeleted = likeDao.deleteByUserIdAndContentId(userId, contentId);

        if (rowsDeleted > 0) {
            // User unliked
            System.out.println("[DEBUG] Like removed for contentId=" + contentId + ", userId=" + userId);
        } else {
            // User liked
            Like like = new Like();
            like.setContentId(contentId);
            like.setUserId(userId);
            like.setCreatedAt(LocalDateTime.now());
            likeDao.insertAndReturn(like);
            
            // Create notification for like
            Long contentOwnerId = contentDao.findOwnerIdByContentId(contentId);
            if (contentOwnerId != null && !contentOwnerId.equals(userId)) {
                notificationService.createLikeNotification(userId, contentOwnerId, contentId, like.getLikeId());
            }
            
            System.out.println("[DEBUG] Like inserted for contentId=" + contentId + ", userId=" + userId);
        }

    // ✅ Redirect back to the correct page
    if (type.equals("post")) {
        return "redirect:/post/" + contentId;
    } else if (type.equals("reel")) {
        return "redirect:/reels/" + contentId;
    } else {
        return "redirect:/";  // fallback
    }
}



  // ---------------- Story toggle (AJAX) ----------------
    @PostMapping("/story/toggle")
    @ResponseBody
    public Map<String, Object> toggleStoryLike(@RequestParam Long contentId,
                                               HttpSession session) {

        Map<String, Object> response = new HashMap<>();
        User user = (User) session.getAttribute("loggedInUser");

        if (user == null) {
            response.put("error", "User not logged in");
            return response;
        }

        Long userId = user.getUser_id();
        int rowsDeleted = likeDao.deleteByUserIdAndContentId(userId, contentId);
        boolean liked = rowsDeleted == 0;

        if (liked) {
            Like like = new Like();
            like.setContentId(contentId);
            like.setUserId(userId);
            like.setCreatedAt(LocalDateTime.now());
            likeDao.insert(like);
        }

        int likeCount = likeDao.countByContentId(contentId);
        response.put("liked", liked);
        response.put("likeCount", likeCount);

        return response;
    }

    // ---------------- Story like count (AJAX) ----------------
    @GetMapping("/story/{storyId}/count")
    @ResponseBody
    public int getStoryLikeCount(@PathVariable Long storyId) {
        return likeDao.countByContentId(storyId);
    }

    // // ✅ Add Like
    // @PostMapping("/add")
    // public String addLike(@RequestParam("postId") Long postId,
    //                     HttpSession session) {
        
    //     System.out.println("🔹 [DEBUG] Entered /like/add controller");
    //     System.out.println("🔹 [DEBUG] Received postId = " + postId);
    //     // get logged-in user
    //     Long userId = ((User) session.getAttribute("user")).getUser_id();

    //     System.out.println("🔹 [DEBUG] Logged-in userId = " + userId);
    //     Like like = new Like();
    //     like.setContentId(postId);   // assuming content_id = postId
    //     like.setUserId(userId);
    //     like.setCreatedAt(LocalDateTime.now());

    //     try {
    //         likeDao.insert(like);
    //         System.out.println("✅ [DEBUG] Like inserted successfully into DB");
    //     } catch (Exception e) {
    //         System.out.println("❌ [DEBUG] Error inserting like: " + e.getMessage());
    //         e.printStackTrace();
    //     }

    //     likeDao.insert(like);

    //     // redirect back to the same post page (or home)
    //     return "redirect:/post/" + postId;
    // }


    // ✅ Remove Like
    @DeleteMapping("/delete/{likeId}")
    public String deleteLike(@PathVariable Long likeId) {
        likeDao.deleteById(likeId);
        return "Like removed successfully!";
    }

    // ✅ Get all likes for a content/post
    @GetMapping("/content/{contentId}")
    public List<Like> getLikesByContent(@PathVariable Long contentId) {
        return likeDao.findByContentId(contentId);
    }

    // ✅ Get like count
    @GetMapping("/content/{contentId}/count")
    public int getLikeCount(@PathVariable Long contentId) {
        return likeDao.countByContentId(contentId);
    }

    // ✅ Get all likes by user
    @GetMapping("/user/{userId}")
    public List<Like> getLikesByUser(@PathVariable Long userId) {
        return likeDao.findByUserId(userId);
    }
}