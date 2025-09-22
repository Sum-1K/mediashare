package com.example.demo.dao;

import com.example.demo.model.Content;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

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
}
