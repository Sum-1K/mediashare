package com.example.demo.dao;

import com.example.demo.model.Like;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.List;
import java.util.Map;
import java.sql.Statement;

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

    // Optional: delete by userId and contentId (unlike)
    public int deleteByUserIdAndContentId(Long userId, Long contentId) {
    String sql = "DELETE FROM like_table WHERE user_id = ? AND content_id = ?";
    return jdbcTemplate.update(sql, userId, contentId);
}


public Long insertAndReturn(Like like) {
    String sql = "INSERT INTO like_table (user_id, content_id, created_at) VALUES (?, ?, ?)";
    KeyHolder keyHolder = new GeneratedKeyHolder();

    jdbcTemplate.update(connection -> {
        PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
        ps.setLong(1, like.getUserId());
        ps.setLong(2, like.getContentId());
        ps.setObject(3, like.getCreatedAt());
        return ps;
    }, keyHolder);

    // ✅ Extract the "like_id" key explicitly from the key map
    Map<String, Object> keyMap = keyHolder.getKeys();
    Long generatedId = null;

    if (keyMap != null && keyMap.containsKey("like_id")) {
        Object keyValue = keyMap.get("like_id");
        if (keyValue instanceof Number) {
            generatedId = ((Number) keyValue).longValue();
        }
    }

    like.setLikeId(generatedId);
    return generatedId;
}

}