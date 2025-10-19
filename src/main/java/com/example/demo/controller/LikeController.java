package com.example.demo.controller;

import com.example.demo.dao.LikeDao;
import com.example.demo.model.Like;
import com.example.demo.model.User;
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

@PostMapping("/toggle")
public String toggleLike(
        @RequestParam Long contentId,         // can be postId or reelId
        @RequestParam String type,            // "post" or "reel"
        HttpSession session) {

    // ✅ Get logged-in user safely
    User user = (User) session.getAttribute("user");
    if (user == null) {
        System.out.println("[ERROR] User not logged in!");
        return "redirect:/login";  // redirect to login if session expired
    }
    Long userId = user.getUser_id();

    // ✅ Remove existing like if it exists
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
        likeDao.insert(like);
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
