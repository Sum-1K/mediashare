package com.example.demo.controller;

import com.example.demo.dao.UserDao;
import com.example.demo.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
public class SearchController {

    @Autowired
    private UserDao userDao;

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
}