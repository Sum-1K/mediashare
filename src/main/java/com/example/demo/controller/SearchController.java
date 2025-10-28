package com.example.demo.controller;

import com.example.demo.dao.UserDao;
import com.example.demo.dao.FollowDao;
import com.example.demo.dao.HashtagDao;
import com.example.demo.dao.ContentHashtagDao;
import com.example.demo.model.Hashtag;
import com.example.demo.model.User;

import jakarta.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Collections;
import java.util.Set;

@RestController
public class SearchController {

    @Autowired
    private UserDao userDao;

    @Autowired
    private HashtagDao hashtagDao;

    @Autowired
    private ContentHashtagDao contentHashtagDao;

    @Autowired
    private FollowDao followDao;

    @GetMapping("/search/users")
    public ResponseEntity<?> searchUsers(@RequestParam String q) {
        try {
            System.out.println("Search query received: " + q);

            if (q == null || q.trim().isEmpty()) {
                return ResponseEntity.ok(List.of());
            }

            // Search users by username, first name, or last name
            List<User> users = userDao.searchUsers(q.trim());

            // Convert to simple DTO for frontend - using CORRECT getter names
            List<Map<String, Object>> results = new ArrayList<>();
            for (User user : users) {
                Map<String, Object> userData = new HashMap<>();
                userData.put("user_id", user.getUser_id());
                userData.put("user_name", user.getUser_name());
                userData.put("photo", user.getPhoto());
                userData.put("first_name", user.getFirst_name());
                userData.put("last_name", user.getLast_name());
                results.add(userData);
            }

            System.out.println("Found " + results.size() + " users");
            return ResponseEntity.ok(results);

        } catch (Exception e) {
            System.out.println("Search error: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.status(500).body(Map.of("error", "Search failed: " + e.getMessage()));
        }
    }

    @GetMapping("/search/hashtags")
    @ResponseBody
    public List<Map<String, Object>> searchHashtags(@RequestParam("q") String query) {
        List<Hashtag> hashtags = hashtagDao.searchByText(query); // like-based search
        List<Map<String, Object>> result = new ArrayList<>();

        for (Hashtag tag : hashtags) {
            int count = contentHashtagDao.countByHashtagId(tag.getHashtagId());
            Map<String, Object> tagData = new HashMap<>();
            tagData.put("text", tag.getText());
            tagData.put("post_count", count);
            result.add(tagData);
        }

        return result;
    }

    @GetMapping("/search/taggable-users")
    @ResponseBody
    public List<User> searchTaggableUsers(@RequestParam String q, HttpSession session) {
        User loggedIn = (User) session.getAttribute("loggedInUser");
        if (loggedIn == null)
            return Collections.emptyList();

        List<User> publicUsers = userDao.searchPublicUsers(q);
        List<User> followees = followDao.searchFollowees(List.of(loggedIn.getUser_id()), q);

        Set<User> combined = new HashSet<>();
        combined.addAll(publicUsers);
        combined.addAll(followees);

        return new ArrayList<>(combined);
    }

}