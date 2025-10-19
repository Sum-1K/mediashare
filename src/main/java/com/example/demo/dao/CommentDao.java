package com.example.demo.dao;

import com.example.demo.model.Comment;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import com.example.demo.dto.CommentDTO;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.List;

@Repository
public class CommentDao extends BaseDao<Comment, Long> {

    @Override
    protected String getTableName() {
        return "comment";
    }

    @Override
    protected String getIdColumn() { 
        return "comment_id";
    }

    @Override
    protected RowMapper<Comment> getRowMapper() {
        return new RowMapper<Comment>() {
            @Override
            public Comment mapRow(ResultSet rs, int rowNum) throws SQLException {
                return new Comment(
                        rs.getLong("comment_id"),
                        rs.getString("content"),
                        rs.getTimestamp("created_at").toLocalDateTime(),
                        rs.getLong("user_id"),
                        rs.getLong("content_id")
                );
            }
        };
    }

    // Insert
    public int insert(Comment comment) {
    String sql = "INSERT INTO \"comment\" (content, created_at, content_id, user_id) VALUES (?, ?, ?, ?)";
    return jdbcTemplate.update(sql,
            comment.getContent(),
            Timestamp.valueOf(comment.getCreatedAt()),
            comment.getContentId(),
            comment.getUserId());
}

    // Update
    public int update(Comment comment) {
        String sql = "UPDATE comment SET content = ?, user_id = ?, content_id = ? WHERE comment_id = ?";
        return jdbcTemplate.update(sql,
                comment.getContent(),
                comment.getUserId(),
                comment.getContentId(),
                comment.getCommentId());
    }

    // Optional: find comments by user
    public List<Comment> findByUserId(Long userId) {
        String sql = "SELECT * FROM comment WHERE user_id = ?";
        return jdbcTemplate.query(sql, getRowMapper(), userId);
    }

    // Optional: find comments by content
    public List<Comment> findByContentId(Long contentId) {
        String sql = "SELECT * FROM comment WHERE content_id = ?";
        return jdbcTemplate.query(sql, getRowMapper(), contentId);
    }

    // Fetch comments joined with usernames
    public List<CommentDTO> findWithUsernameByContentId(Long contentId) {
        String sql = """
            SELECT c.comment_id, c.content, c.created_at, c.content_id, c.user_id, u.user_name AS username
            FROM comment c
            JOIN users u ON c.user_id = u.user_id
            WHERE c.content_id = ?
            ORDER BY c.created_at DESC
        """;

        return jdbcTemplate.query(sql, (rs, rowNum) -> new CommentDTO(
                rs.getLong("comment_id"),
                rs.getString("content"),
                rs.getTimestamp("created_at").toLocalDateTime(),
                rs.getLong("content_id"),
                rs.getLong("user_id"),
                rs.getString("username")
        ), contentId);
    }
}