package com.example.demo.service;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import com.example.demo.dto.UserDTO;




@Service
public class UserService {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    // Fetch followers
    public List<UserDTO> getFollowers(Long userId) {
        String sql = "SELECT u.*, f.isCloseFriend " +
                     "FROM users u " +
                     "JOIN followers f ON u.id = f.follower_id " +
                     "WHERE f.following_id = ?";
        return jdbcTemplate.query(sql, new Object[]{userId}, 
            (rs, rowNum) -> new UserDTO(
                rs.getLong("id"),
                rs.getString("username"),
                rs.getBoolean("isCloseFriend")
            ));
    }

    // Fetch following
    public List<UserDTO> getFollowing(Long userId) {
        String sql = "SELECT u.*, f.isCloseFriend " +
                     "FROM users u " +
                     "JOIN followers f ON u.id = f.following_id " +
                     "WHERE f.follower_id = ?";
        return jdbcTemplate.query(sql, new Object[]{userId}, 
            (rs, rowNum) -> new UserDTO(
                rs.getLong("id"),
                rs.getString("username"),
                rs.getBoolean("isCloseFriend")
            ));
    }

    // Add or remove close friend
    public void setCloseFriend(Long followerId, Long followingId, boolean isCloseFriend) {
        String sql = "UPDATE followers SET isCloseFriend = ? WHERE follower_id = ? AND following_id = ?";
        jdbcTemplate.update(sql, isCloseFriend, followerId, followingId);
    }

    // Block user
    public void blockUser(Long blockerId, Long blockedId) {
        String sql = "INSERT INTO blocked_users(blocker_id, blocked_id) VALUES(?, ?) ON CONFLICT DO NOTHING";
        jdbcTemplate.update(sql, blockerId, blockedId);
    }
}

