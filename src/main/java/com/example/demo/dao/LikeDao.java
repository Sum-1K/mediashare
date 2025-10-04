package com.example.demo.dao;

import com.example.demo.model.Like;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.List;

@Repository
public class LikeDao extends BaseDao<Like, Long> {

    @Override
    protected String getTableName() {
        return "like_table";  // table name in DB
    }

    @Override
    protected String getIdColumn() {
        return "like_id";
    }

    @Override
    protected RowMapper<Like> getRowMapper() {
        return new RowMapper<Like>() {
            @Override
            public Like mapRow(ResultSet rs, int rowNum) throws SQLException {
                return new Like(
                        rs.getLong("like_id"),
                        rs.getTimestamp("created_at").toLocalDateTime(),
                        rs.getLong("user_id"),
                        rs.getLong("content_id")
                );
            }
        };
    }

    // Insert
    public int insert(Like like) {
        String sql = "INSERT INTO like_table (created_at, user_id, content_id) VALUES (?, ?, ?)";
        return jdbcTemplate.update(sql,
                Timestamp.valueOf(like.getCreatedAt()),
                like.getUserId(),
                like.getContentId());
    }

    // Update
    public int update(Like like) {
        String sql = "UPDATE like_table SET user_id = ?, content_id = ? WHERE like_id = ?";
        return jdbcTemplate.update(sql,
                like.getUserId(),
                like.getContentId(),
                like.getLikeId());
    }

    // Optional: find all likes for a user
    public List<Like> findByUserId(Long userId) {
        String sql = "SELECT * FROM like_table WHERE user_id = ?";
        return jdbcTemplate.query(sql, getRowMapper(), userId);
    }

    // Optional: find all likes for a content
    public List<Like> findByContentId(Long contentId) {
        String sql = "SELECT * FROM like_table WHERE content_id = ?";
        return jdbcTemplate.query(sql, getRowMapper(), contentId);
    }

    public int countByContentId(Long contentId) {
        String sql = "SELECT COUNT(*) FROM like_table WHERE content_id = ?";
        return jdbcTemplate.queryForObject(sql, Integer.class, contentId);
    }
}