package com.example.demo.controller;

import com.example.demo.dao.FollowDao;
import com.example.demo.model.User;
import com.example.demo.service.FollowService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.example.demo.dao.UserDao;
import com.example.demo.model.User;
import com.example.demo.service.FollowService;

import jakarta.servlet.http.HttpSession;

@Controller
@RequestMapping("/follow")
public class FollowController {
    
    @Autowired
    private FollowService followService;

    @Autowired
    private FollowDao followDao;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Autowired
    private UserDao userDao;
    
    @PostMapping("/{userId}")
    @ResponseBody
    public Map<String, Object> followUser(@PathVariable Long userId, HttpSession session) {
        Map<String, Object> response = new HashMap<>();
        
        User currentUser = (User) session.getAttribute("user");
        if (currentUser == null) {
            response.put("success", false);
            response.put("message", "Please login first");
            return response;
        }
        
        // Prevent self-follow
        if (currentUser.getUser_id().equals(userId)) {
            response.put("success", false);
            response.put("message", "Cannot follow yourself");
            return response;
        }
        
        try {
            String result = followService.followUser(currentUser.getUser_id(), userId);
            boolean isFollowing = result.equals("Followed successfully");
            boolean hasPendingRequest = result.equals("Follow request sent");
            
            response.put("success", true);
            response.put("message", result);
            response.put("isFollowing", isFollowing);
            response.put("hasPendingRequest", hasPendingRequest);
            
            // Update counts
            response.put("followerCount", followService.getFollowerCount(userId));
            
        } catch (Exception e) {
            e.printStackTrace();
            response.put("success", false);
            response.put("message", "Error following user");
        }
        
        return response;
    }
    
    @PostMapping("/unfollow/{userId}")
    @ResponseBody
    public Map<String, Object> unfollowUser(@PathVariable Long userId, HttpSession session) {
        Map<String, Object> response = new HashMap<>();
        
        User currentUser = (User) session.getAttribute("user");
        if (currentUser == null) {
            response.put("success", false);
            response.put("message", "Please login first");
            return response;
        }
        
        try {
            String result = followService.unfollowUser(currentUser.getUser_id(), userId);
            response.put("success", true);
            response.put("message", result);
            response.put("isFollowing", false);
            response.put("hasPendingRequest", false);
            
            // Update counts
            response.put("followerCount", followService.getFollowerCount(userId));
            
        } catch (Exception e) {
            e.printStackTrace();
            response.put("success", false);
            response.put("message", "Error unfollowing user");
        }
        
        return response;
    }
    
    @PostMapping("/request/{requestId}/accept")
    @ResponseBody
    public Map<String, Object> acceptFollowRequest(@PathVariable Long requestId, HttpSession session) {
        Map<String, Object> response = new HashMap<>();
        
        User currentUser = (User) session.getAttribute("user");
        if (currentUser == null) {
            response.put("success", false);
            response.put("message", "Please login first");
            return response;
        }
        
        try {
            String result = followService.acceptFollowRequest(requestId);
            response.put("success", true);
            response.put("message", result);
        } catch (Exception e) {
            e.printStackTrace();
            response.put("success", false);
            response.put("message", "Error accepting request");
        }
        
        return response;
    }
    
    @PostMapping("/request/{requestId}/reject")
    @ResponseBody
    public Map<String, Object> rejectFollowRequest(@PathVariable Long requestId, HttpSession session) {
        Map<String, Object> response = new HashMap<>();
        
        User currentUser = (User) session.getAttribute("user");
        if (currentUser == null) {
            response.put("success", false);
            response.put("message", "Please login first");
            return response;
        }
        
        try {
            String result = followService.rejectFollowRequest(requestId);
            response.put("success", true);
            response.put("message", result);
        } catch (Exception e) {
            e.printStackTrace();
            response.put("success", false);
            response.put("message", "Error rejecting request");
        }
        
        return response;
    }
    
    @GetMapping("/requests")
    public String showFollowRequests(HttpSession session, Model model) {
        User currentUser = (User) session.getAttribute("user");
        if (currentUser == null) {
            return "redirect:/users/login";
        }
        
        System.out.println("=== DEBUG: Getting follow requests for user: " + currentUser.getUser_id());
        
        List<Map<String, Object>> followRequestsWithUsers = followService.getFollowRequestsWithUserInfo(currentUser.getUser_id());
        
        System.out.println("=== DEBUG: Found " + followRequestsWithUsers.size() + " follow requests with user info");
        
        model.addAttribute("followRequestsWithUsers", followRequestsWithUsers);
        return "follow_requests";
    }
    
    @GetMapping("/status/{userId}")
    @ResponseBody
    public Map<String, Object> getFollowStatus(@PathVariable Long userId, HttpSession session) {
        Map<String, Object> response = new HashMap<>();
        
        User currentUser = (User) session.getAttribute("user");
        if (currentUser == null) {
            response.put("success", false);
            response.put("message", "Please login first");
            return response;
        }
        
        try {
            boolean isFollowing = followService.isFollowing(currentUser.getUser_id(), userId);
            boolean hasPendingRequest = followService.hasPendingRequest(currentUser.getUser_id(), userId);
            int followerCount = followService.getFollowerCount(userId);
            int followingCount = followService.getFollowingCount(userId);
            User.Privacy privacy = userDao.getPrivacy(userId);
            
            response.put("success", true);
            response.put("isFollowing", isFollowing);
            response.put("hasPendingRequest", hasPendingRequest);
            response.put("followerCount", followerCount);
            response.put("followingCount", followingCount);
            response.put("privacy", privacy.name());
        } catch (Exception e) {
            e.printStackTrace();
            response.put("success", false);
            response.put("message", "Error getting follow status");
        }
        
        return response;
    }

    @GetMapping("/test")
    @ResponseBody
    public String testEndpoint() {
        return "Follow controller is working!";
    }

    @GetMapping("/debug/requests")
    @ResponseBody
    public String debugRequests(HttpSession session) {
        User currentUser = (User) session.getAttribute("user");
        if (currentUser == null) {
            return "Not logged in";
        }
        
        try {
            String sql = "SELECT * FROM follow_requests WHERE receiver_id = ? AND status = 'SENT'";
            List<Map<String, Object>> results = jdbcTemplate.queryForList(sql, currentUser.getUser_id());
            
            StringBuilder response = new StringBuilder();
            response.append("Database results for user ").append(currentUser.getUser_id()).append(":\n");
            for (Map<String, Object> row : results) {
                response.append("Request: ").append(row).append("\n");
            }
            return response.toString();
        } catch (Exception e) {
            return "Error: " + e.getMessage();
        }
    }

    @GetMapping("/users")
    @ResponseBody
    public List<User> searchUsers(
            @RequestParam String prefix,
            HttpSession session
    ) {
        // Get current logged-in user from session
        User loggedInUser = (User) session.getAttribute("loggedInUser");
        if (loggedInUser == null) {
            throw new RuntimeException("User not logged in");
        }

        Long userId = loggedInUser.getUser_id();
        return followDao.searchFollowersAndFollowees(userId, prefix);
    }  
}