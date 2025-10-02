package com.example.demo.dao;

import com.example.demo.model.FollowRequest;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import java.sql.PreparedStatement;
import java.sql.Timestamp;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

@Repository
public class FollowRequestDao extends BaseDao<FollowRequest, Long> {

    @Override
    protected String getTableName() {
        return "follow_requests";
    }

    @Override
    protected String getIdColumn() {
        return "request_id";
    }

    @Override
    protected RowMapper<FollowRequest> getRowMapper() {
        return new RowMapper<FollowRequest>() {
            @Override
            public FollowRequest mapRow(ResultSet rs, int rowNum) throws SQLException {
                FollowRequest request = new FollowRequest();
                request.setRequest_id(rs.getLong("request_id"));
                request.setSender_id(rs.getLong("sender_id"));
                request.setReceiver_id(rs.getLong("receiver_id"));
                request.setStatus(rs.getString("status"));
                request.setCreated_at(rs.getTimestamp("created_at").toLocalDateTime());
                return request;
            }
        };
    }

    public List<FollowRequest> findPendingRequests(Long userId) {
        String sql = "SELECT * FROM " + getTableName() + " WHERE receiver_id = ? AND status = 'SENT'";
        return jdbcTemplate.query(sql, getRowMapper(), userId);
    }

    public List<FollowRequest> findByReceiverId(Long receiverId) {
        try {
            System.out.println("=== DAO DEBUG: Finding requests for receiver: " + receiverId);
            
            // Use the correct status for Supabase - 'SENT' instead of 'PENDING'
            String sql = "SELECT * FROM " + getTableName() + " WHERE receiver_id = ? AND status = 'SENT'";
            
            System.out.println("Executing SQL: " + sql);
            
            List<FollowRequest> requests = jdbcTemplate.query(sql, getRowMapper(), receiverId);
            
            System.out.println("=== DAO DEBUG: Found " + requests.size() + " requests");
            for (FollowRequest request : requests) {
                System.out.println("Request: " + request.getRequest_id() + 
                                " from " + request.getSender_id() + 
                                " to " + request.getReceiver_id() +
                                " status: " + request.getStatus());
            }
            
            return requests;
        } catch (Exception e) {
            System.err.println("ERROR in findByReceiverId: " + e.getMessage());
            e.printStackTrace();
            return List.of();
        }
    }

    public boolean hasPendingRequest(Long senderId, Long receiverId) {
        String sql = "SELECT COUNT(*) FROM " + getTableName() + " WHERE sender_id = ? AND receiver_id = ? AND status = 'SENT'";
        Integer count = jdbcTemplate.queryForObject(sql, Integer.class, senderId, receiverId);
        return count != null && count > 0;
    }

    public int update(FollowRequest request) {
        String sql = "UPDATE " + getTableName() + " SET status = ? WHERE request_id = ?";
        return jdbcTemplate.update(sql, request.getStatus(), request.getRequest_id());
    }

    // Override save method to handle auto-generated ID
    // Override save method to handle auto-generated ID
    // In FollowRequestDao.java - make sure the save method looks like this:
    // Updated save method for Supabase
    public Long save(FollowRequest request) {
        // Use the correct column order for Supabase
        final String sql = "INSERT INTO " + getTableName() + " (sender_id, receiver_id, status, created_at, sent_at) VALUES (?, ?, ?, ?, ?)";
        KeyHolder keyHolder = new GeneratedKeyHolder();
        
        jdbcTemplate.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(sql, new String[]{"request_id"});
            ps.setLong(1, request.getSender_id());
            ps.setLong(2, request.getReceiver_id());
            ps.setString(3, request.getStatus());
            ps.setTimestamp(4, java.sql.Timestamp.valueOf(request.getCreated_at()));
            ps.setTimestamp(5, java.sql.Timestamp.valueOf(request.getSent_at()));
            return ps;
        }, keyHolder);
        
        Number key = keyHolder.getKey();
        return key != null ? key.longValue() : null;
    }

    public FollowRequest findById(Long requestId) {
        String sql = "SELECT * FROM " + getTableName() + " WHERE request_id = ?";
        List<FollowRequest> requests = jdbcTemplate.query(sql, getRowMapper(), requestId);
        return requests.isEmpty() ? null : requests.get(0);
    }
}