package com.example.demo.dao;

import com.example.demo.model.Content;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.List;

@Repository
public class ContentDao extends BaseDao<Content, Long> {

    private final RowMapper<Content> contentRowMapper = new RowMapper<>() {
        @Override
        public Content mapRow(ResultSet rs, int rowNum) throws SQLException {
            Content content = new Content();
            content.setContentId(rs.getLong("content_id"));
            content.setCreatedAt(rs.getTimestamp("created_at").toLocalDateTime());
            content.setUserId(rs.getLong("user_id"));
            return content;
        }
    };

    @Override
    protected String getTableName() {
        return "content";
    }

    @Override
    protected String getIdColumn() {
        return "content_id";
    }

    @Override
    protected RowMapper<Content> getRowMapper() {
        return contentRowMapper;
    }

    // CREATE (insert new content)

    public Long saveAndReturnId(Content content) {
        String sql = "INSERT INTO content (user_id, created_at) VALUES (?, ?)";

        LocalDateTime now = LocalDateTime.now();
        content.setCreatedAt(now);

        KeyHolder keyHolder = new GeneratedKeyHolder();

        jdbcTemplate.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(sql, new String[]{"content_id"});
            ps.setLong(1, content.getUserId());
            ps.setObject(2, now);
            return ps;
        }, keyHolder);

        Long generatedId = keyHolder.getKey().longValue();
        content.setContentId(generatedId);
        return generatedId;
    }


    public int save(Content content) {
        String sql = "INSERT INTO content (user_id, created_at) VALUES (?, ?)";

        LocalDateTime now = LocalDateTime.now();
        content.setCreatedAt(now);

        return jdbcTemplate.update(sql, content.getUserId(), now);
    }

    // UPDATE
    public int update(Content content) {
        String sql = "UPDATE content SET user_id = ? WHERE content_id = ?";
        return jdbcTemplate.update(sql, content.getUserId(), content.getContentId());
    }

    // Add this method to ContentDao.java
    public Long findOwnerIdByContentId(Long contentId) {
        String sql = "SELECT user_id FROM content WHERE content_id = ?";
        try {
            return jdbcTemplate.queryForObject(sql, Long.class, contentId);
        } catch (Exception e) {
            System.err.println("Error finding content owner for contentId: " + contentId);
            return null;
        }
    }

    
}
