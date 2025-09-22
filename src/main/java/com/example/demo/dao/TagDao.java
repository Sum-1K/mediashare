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
                return tag;
            }
        };
    }

    // Insert new Tag
    public int save(Tag tag) {
        String sql = "INSERT INTO " + getTableName() + " (user_id, content_id) VALUES (?, ?)";
        return jdbcTemplate.update(sql, tag.getUser_id(), tag.getContent_id());
    }

    // Find all tags for a content
    public List<Tag> findByContent(Long contentId) {
        String sql = "SELECT * FROM " + getTableName() + " WHERE content_id = ?";
        return jdbcTemplate.query(sql, getRowMapper(), contentId);
    }

    // Find all tags for a user
    public List<Tag> findByUser(Long userId) {
        String sql = "SELECT * FROM " + getTableName() + " WHERE user_id = ?";
        return jdbcTemplate.query(sql, getRowMapper(), userId);
    }
}