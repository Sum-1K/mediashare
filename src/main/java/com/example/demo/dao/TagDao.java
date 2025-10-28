package com.example.demo.dao;

import com.example.demo.model.Tag;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

@Repository
public class TagDao extends BaseDao<Tag, Long> {

    @Override
    protected String getTableName() {
        return "tag";
    }

    @Override
    protected String getIdColumn() {
        return "tag_id";
    }

    @Override
    protected RowMapper<Tag> getRowMapper() {
        return new RowMapper<Tag>() {
            @Override
            public Tag mapRow(ResultSet rs, int rowNum) throws SQLException {
                Tag tag = new Tag();
                tag.setTag_id(rs.getLong("tag_id"));
                tag.setUser_id(rs.getLong("user_id"));
                tag.setContent_id(rs.getLong("content_id"));
                tag.setStatus(rs.getString("status"));
                java.sql.Timestamp ts = rs.getTimestamp("created_at");
                if (ts != null) {
                    tag.setCreatedAt(ts.toLocalDateTime());
                }
                return tag;
            }
        };
    }

    // Insert new Tag
    public int save(Tag tag) {
        String sql = "INSERT INTO " + getTableName() + " (user_id, content_id, status, created_at) VALUES (?, ?, 'PENDING', NOW())";  //PENDING, ACCEPTED, DECLINED
        return jdbcTemplate.update(sql, tag.getUser_id(), tag.getContent_id());
    }

    // Find all tags for a user
    public List<Tag> findByUser(Long userId) {
        String sql = "SELECT * FROM " + getTableName() + " WHERE user_id = ?";
        return jdbcTemplate.query(sql, getRowMapper(), userId);
    }

    public List<Tag> findPendingByUser(Long userId) {
        String sql = "SELECT * FROM " + getTableName() + " WHERE user_id=? AND status='PENDING'";
        return jdbcTemplate.query(sql, getRowMapper(), userId);
    }

    public int updateStatus(Long tagId, String status) {
        String sql = "UPDATE tag SET status=? WHERE tag_id=?";
        return jdbcTemplate.update(sql, status, tagId);
    }

    public List<Tag> findByContentId(Long contentId) {
        String sql = "SELECT * FROM tag WHERE content_id=?";
        return jdbcTemplate.query(sql, getRowMapper(), contentId);
    }
}