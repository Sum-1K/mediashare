package com.example.demo.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.example.demo.dao.MediaDao;
import com.example.demo.dao.PostDao;
import com.example.demo.dao.SavedPostDao;
import com.example.demo.model.Media;
import com.example.demo.model.Post;
import com.example.demo.model.SavedPost;
import com.example.demo.model.User; // ✅ add this

import jakarta.servlet.http.HttpSession;

@Controller
@RequestMapping("/saved")
public class SavedPostController {

    private final SavedPostDao savedPostDao;
    private final PostDao postDao;
    private final MediaDao mediaDao;

    public SavedPostController(SavedPostDao savedPostDao, PostDao postDao, MediaDao mediaDao) {
        this.savedPostDao = savedPostDao;
        this.postDao = postDao;
        this.mediaDao = mediaDao;
    }

    // Save a post
    @PostMapping("/save/{postId}")
@ResponseBody
public Map<String, Object> savePost(@PathVariable Long postId, HttpSession session) {
    Map<String, Object> response = new HashMap<>();  // ✅ new map to return JSON
    User user = (User) session.getAttribute("loggedInUser");

    if (user == null) {
        response.put("success", false);           // ✅ include success flag
        response.put("message", "Please login first"); // ✅ include message
        return response;                           // ✅ return JSON
    }

    try {
        SavedPost sp = new SavedPost(user.getUser_id(), postId);
        savedPostDao.save(sp);
        response.put("success", true);            // ✅ indicate success
        response.put("message", "Post saved!");   // ✅ include message
    } catch (Exception e) {
        response.put("success", false);           // ✅ handle exceptions
        response.put("message", "Error: " + e.getMessage());
    }

    return response;                               // ✅ return JSON
}



    // Unsave a post
    @PostMapping("/unsave/{postId}")
@ResponseBody
public Map<String, Object> unsavePost(@PathVariable Long postId, HttpSession session) {
    Map<String, Object> response = new HashMap<>();  // ✅ new map to return JSON
    User user = (User) session.getAttribute("loggedInUser");

    if (user == null) {
        response.put("success", false);           // ✅ include success flag
        response.put("message", "Please login");  // ✅ include message
        return response;                           // ✅ return JSON
    }

    try {
        savedPostDao.deleteById(user.getUser_id(), postId);
        response.put("success", true);            // ✅ indicate success
        response.put("message", "Post removed from saved"); // ✅ message
    } catch (Exception e) {
        response.put("success", false);           // ✅ handle exceptions
        response.put("message", "Error: " + e.getMessage());
    }

    return response;                               // ✅ return JSON
}


    // View saved posts
    @GetMapping("/my")
public String viewSavedPosts(Model model, HttpSession session) {
    User user = (User) session.getAttribute("loggedInUser");
    if (user == null) return "redirect:/login";

    // Get all saved post IDs
    List<Long> savedPostIds = savedPostDao.getSavedPostIds(user.getUser_id());

    // Retrieve Post objects
    List<Post> savedPosts = savedPostIds.stream()
            .map(postDao::findById)
            .collect(Collectors.toList());

    // Build postId → mediaList map, similar to single post logic
    Map<Long, List<Media>> postMediaMap = new HashMap<>();
    for (Post post : savedPosts) {
        List<Media> mediaList = mediaDao.findByPostId(post.getPostId());
        postMediaMap.put(post.getPostId(), mediaList);
    }

    model.addAttribute("posts", savedPosts);
    model.addAttribute("postMediaMap", postMediaMap); // ✅ same as mediaList but for each post
    model.addAttribute("timestamp", System.currentTimeMillis());

    return "savedPosts";
}

// ✅ Check if a post is already saved
@GetMapping("/status")
@ResponseBody
public Map<String, Object> checkSavedStatus(Long postId, HttpSession session) {
    Map<String, Object> response = new HashMap<>();
    User user = (User) session.getAttribute("loggedInUser");

    if (user == null) {
        response.put("saved", false);
        return response;
    }

    boolean isSaved = savedPostDao.isPostSaved(user.getUser_id(), postId);
    response.put("saved", isSaved);
    return response;
}


}
