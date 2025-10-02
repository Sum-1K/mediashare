package com.example.demo.service;

import com.example.demo.dao.FollowDao;
import com.example.demo.dao.FollowRequestDao;
import com.example.demo.dao.UserDao;
import com.example.demo.model.Follow;
import com.example.demo.model.FollowRequest;
import com.example.demo.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class FollowService {
    
    @Autowired
    private FollowDao followDao;
    
    @Autowired
    private FollowRequestDao followRequestDao;
    
    @Autowired
    private UserDao userDao;

    @Autowired
    private JdbcTemplate jdbcTemplate;
    
    public String followUser(Long followerId, Long followingId) {
        try {
            System.out.println("=== FOLLOW SERVICE DEBUG ===");
            System.out.println("followerId: " + followerId + ", followingId: " + followingId);
            
            // Check if users exist
            User follower = userDao.findById(followerId);
            User following = userDao.findById(followingId);
            
            System.out.println("Follower user: " + follower);
            System.out.println("Following user: " + following);
            
            if (follower == null || following == null) {
                System.out.println("User not found");
                return "User not found";
            }
            
            // Check if already following
            boolean isAlreadyFollowing = followDao.isFollowing(followerId, followingId);
            System.out.println("Already following: " + isAlreadyFollowing);
            
            if (isAlreadyFollowing) {
                return "Already following";
            }
            
            // Handle based on account privacy
            System.out.println("Following user privacy: " + following.getPrivacy());
            
            if (following.getPrivacy() == User.Privacy.PUBLIC) {
                // Direct follow for public accounts
                System.out.println("Creating public follow...");
                Follow follow = new Follow();
                follow.setFollowerId(followerId);
                follow.setFolloweeId(followingId);
                follow.setCloseFriend(false);
                follow.setSince(LocalDateTime.now());
                
                int result = followDao.save(follow);
                System.out.println("Follow save result: " + result);
                return "Followed successfully";
            } else {
                // Follow request for private accounts
                System.out.println("Creating follow request...");
                FollowRequest request = new FollowRequest();
                request.setSender_id(followerId);
                request.setReceiver_id(followingId);
                request.setStatus("SENT");
                request.setCreated_at(LocalDateTime.now());
                request.setSent_at(LocalDateTime.now());
                
                Long requestId = followRequestDao.save(request);
                System.out.println("Follow request save result, ID: " + requestId);
                return "Follow request sent";
            }
        } catch (Exception e) {
            System.err.println("ERROR in followUser: " + e.getMessage());
            e.printStackTrace();
            throw new RuntimeException("Error following user", e);
        }
    }
    
    public String unfollowUser(Long followerId, Long followingId) {
        try {
            boolean success = followDao.delete(followerId, followingId);
            return success ? "Unfollowed successfully" : "Not following this user";
        } catch (Exception e) {
            System.err.println("ERROR in unfollowUser: " + e.getMessage());
            e.printStackTrace();
            throw new RuntimeException("Error unfollowing user", e);
        }
    }
    
    public List<FollowRequest> getPendingRequests(Long userId) {
        try {
            System.out.println("Getting pending requests for user: " + userId);
            List<FollowRequest> requests = followRequestDao.findPendingRequests(userId);
            System.out.println("Found requests: " + requests.size());
            return requests;
        } catch (Exception e) {
            System.err.println("ERROR in getPendingRequests: " + e.getMessage());
            e.printStackTrace();
            throw new RuntimeException("Error getting follow requests", e);
        }
    }
    
    public String acceptFollowRequest(Long requestId) {
        try {
            FollowRequest request = followRequestDao.findById(requestId);
            if (request != null && "SENT".equals(request.getStatus())) {
                // Create follow relationship
                Follow follow = new Follow();
                follow.setFollowerId(request.getSender_id());
                follow.setFolloweeId(request.getReceiver_id());
                follow.setCloseFriend(false);
                follow.setSince(LocalDateTime.now());
                followDao.save(follow);
                
                // Update request status
                request.setStatus("ACCEPTED");
                followRequestDao.update(request);
                return "Follow request accepted";
            }
            return "Request not found or already processed";
        } catch (Exception e) {
            System.err.println("ERROR in acceptFollowRequest: " + e.getMessage());
            e.printStackTrace();
            throw new RuntimeException("Error accepting follow request", e);
        }
    }
    
    public String rejectFollowRequest(Long requestId) {
        try {
            FollowRequest request = followRequestDao.findById(requestId);
            if (request != null && "SENT".equals(request.getStatus())) {
                request.setStatus("DECLINED");
                followRequestDao.update(request);
                return "Follow request declined";
            }
            return "Request not found or already processed";
        } catch (Exception e) {
            System.err.println("ERROR in rejectFollowRequest: " + e.getMessage());
            e.printStackTrace();
            throw new RuntimeException("Error declining follow request", e);
        }
    }
    
    public boolean isFollowing(Long followerId, Long followingId) {
        try {
            return followDao.isFollowing(followerId, followingId);
        } catch (Exception e) {
            System.err.println("ERROR in isFollowing: " + e.getMessage());
            return false;
        }
    }
    
    public boolean hasPendingRequest(Long senderId, Long receiverId) {
        try {
            return followRequestDao.hasPendingRequest(senderId, receiverId);
        } catch (Exception e) {
            System.err.println("ERROR in hasPendingRequest: " + e.getMessage());
            return false;
        }
    }
    
    public int getFollowerCount(Long userId) {
        try {
            return followDao.getFollowerCount(userId);
        } catch (Exception e) {
            System.err.println("ERROR in getFollowerCount: " + e.getMessage());
            return 0;
        }
    }
    
    public int getFollowingCount(Long userId) {
        try {
            return followDao.getFollowingCount(userId);
        } catch (Exception e) {
            System.err.println("ERROR in getFollowingCount: " + e.getMessage());
            return 0;
        }
    }
    
    public List<User> getFollowers(Long userId) {
        try {
            return followDao.getFollowers(userId);
        } catch (Exception e) {
            System.err.println("ERROR in getFollowers: " + e.getMessage());
            return List.of();
        }
    }
    
    public List<User> getFollowing(Long userId) {
        try {
            return followDao.getFollowing(userId);
        } catch (Exception e) {
            System.err.println("ERROR in getFollowing: " + e.getMessage());
            return List.of();
        }
    }
    
    public List<FollowRequest> getFollowRequests(Long userId) {
        try {
            System.out.println("Getting follow requests for user: " + userId);
            List<FollowRequest> requests = followRequestDao.findByReceiverId(userId);
            System.out.println("Raw requests found: " + requests.size());
            
            // For debugging, let's see what's in the database directly
            String debugSql = "SELECT * FROM follow_requests WHERE receiver_id = ? AND status = 'SENT'";
            List<Map<String, Object>> debugResults = jdbcTemplate.queryForList(debugSql, userId);
            System.out.println("Database results: " + debugResults);
            
            return requests;
        } catch (Exception e) {
            System.err.println("ERROR in getFollowRequests: " + e.getMessage());
            e.printStackTrace();
            return List.of();
        }
    }

    // NEW METHOD: Get follow requests with user information
    public List<Map<String, Object>> getFollowRequestsWithUserInfo(Long userId) {
        try {
            System.out.println("Getting follow requests with user info for user: " + userId);
            List<FollowRequest> requests = followRequestDao.findByReceiverId(userId);
            System.out.println("Raw requests found: " + requests.size());
            
            List<Map<String, Object>> requestsWithUsers = new ArrayList<>();
            
            // Load user information for each sender
            for (FollowRequest request : requests) {
                User sender = userDao.findById(request.getSender_id());
                if (sender != null) {
                    Map<String, Object> requestMap = new HashMap<>();
                    requestMap.put("request", request);
                    requestMap.put("sender", sender);
                    requestsWithUsers.add(requestMap);
                    System.out.println("Loaded sender: " + sender.getUser_name() + " for request " + request.getRequest_id());
                }
            }
            
            return requestsWithUsers;
        } catch (Exception e) {
            System.err.println("ERROR in getFollowRequestsWithUserInfo: " + e.getMessage());
            e.printStackTrace();
            return List.of();
        }
    }
}