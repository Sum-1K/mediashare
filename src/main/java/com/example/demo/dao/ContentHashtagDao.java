package com.example.demo.dao;

import com.example.demo.model.ContentHashtag;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;

@Repository
public class ContentHashtagDao extends BaseDao<ContentHashtag, Long> {

    @Override
    protected String getTableName() {
        return "content_hashtag";
    }

    @Override
    protected String getIdColumn() {
        // composite key, so this won’t really be used directly
        return "content_id";
    }

    @Override
    protected RowMapper<ContentHashtag> getRowMapper() {
        return new RowMapper<ContentHashtag>() {
            @Override
            public ContentHashtag mapRow(ResultSet rs, int rowNum) throws SQLException {
                ContentHashtag ch = new ContentHashtag();
                ch.setContent_id(rs.getLong("content_id"));
                ch.setHashtag_id(rs.getLong("hashtag_id"));
                return ch;
            }
        };
    }

    // Custom methods for composite key
    public ContentHashtag findByContentAndHashtag(Long contentId, Long hashtagId) {
        String sql = "SELECT * FROM " + getTableName() + " WHERE content_id = ? AND hashtag_id = ?";
        return jdbcTemplate.queryForObject(sql, getRowMapper(), contentId, hashtagId);
    }

    public int deleteByContentAndHashtag(Long contentId, Long hashtagId) {
        String sql = "DELETE FROM " + getTableName() + " WHERE content_id = ? AND hashtag_id = ?";
        return jdbcTemplate.update(sql, contentId, hashtagId);
    }

    public int save(ContentHashtag ch) {
        String sql = "INSERT INTO " + getTableName() + " (content_id, hashtag_id) VALUES (?, ?)";
        return jdbcTemplate.update(sql, ch.getContent_id(), ch.getHashtag_id());
    }
}