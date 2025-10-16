package com.example.demo.dao;

import com.example.demo.model.SavedPost;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.stream.Collectors;

@Repository
public class SavedPostDao extends BaseDao<SavedPost, Long> {

    @Override
    protected String getTableName() {
        return "saved_post";
    }

    @Override
    protected String getIdColumn() {
        // Not relevant for composite key, but required by BaseDao
        return "user_id";
    }

    @Override
    protected RowMapper<SavedPost> getRowMapper() {
        return new RowMapper<SavedPost>() {
            @Override
            public SavedPost mapRow(ResultSet rs, int rowNum) throws SQLException {
                SavedPost savedPost = new SavedPost();
                savedPost.setUserId(rs.getLong("user_id"));
                savedPost.setSavedPostId(rs.getLong("saved_post_id"));
                return savedPost;
            }
        };
    }

    // ✅ Save new saved post record
    public int save(SavedPost savedPost) {
        String sql = "INSERT INTO " + getTableName() + " (user_id, saved_post_id) VALUES (?, ?)";
        return jdbcTemplate.update(sql,
                savedPost.getUserId(),
                savedPost.getSavedPostId());
    }

    // ✅ Find by composite key
    public SavedPost findById(Long userId, Long savedPostId) {
        String sql = "SELECT * FROM " + getTableName() + " WHERE user_id = ? AND saved_post_id = ?";
        return jdbcTemplate.queryForObject(sql, getRowMapper(), userId, savedPostId);
    }

    // ✅ Delete by composite key
    public int deleteById(Long userId, Long savedPostId) {
        String sql = "DELETE FROM " + getTableName() + " WHERE user_id = ? AND saved_post_id = ?";
        return jdbcTemplate.update(sql, userId, savedPostId);
    }

    // ✅ Get all posts saved by a given user
    public List<SavedPost> findAllByUser(Long userId) {
        String sql = "SELECT * FROM " + getTableName() + " WHERE user_id = ?";
        return jdbcTemplate.query(sql, getRowMapper(), userId);
    }

    // ✅ Get all users who saved a given post
    public List<SavedPost> findAllByPost(Long savedPostId) {
        String sql = "SELECT * FROM " + getTableName() + " WHERE saved_post_id = ?";
        return jdbcTemplate.query(sql, getRowMapper(), savedPostId);
    }

    public List<Long> getSavedPostIds(Long userId) {
    List<SavedPost> savedPosts = findAllByUser(userId);
    return savedPosts.stream()
                     .map(SavedPost::getSavedPostId)
                     .collect(Collectors.toList());
}

public boolean isPostSaved(Long userId, Long postId) {
    String sql = "SELECT COUNT(*) FROM saved_post WHERE user_id = ? AND saved_post_id = ?";
    Integer count = jdbcTemplate.queryForObject(sql, Integer.class, userId, postId);
    return count != null && count > 0;
}



}