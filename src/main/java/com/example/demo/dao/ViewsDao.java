package com.example.demo.dao;

import com.example.demo.model.Views;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;

@Repository
public class ViewsDao extends BaseDao<Views, Long> {

    @Override
    protected String getTableName() {
        return "views";
    }

    @Override
    protected String getIdColumn() {
        // Since Views has a composite key, you might not really use this directly.
        // But if needed, you can choose "user_id" or handle both in custom queries.
        return "user_id";
    }

    @Override
    protected RowMapper<Views> getRowMapper() {
        return new RowMapper<Views>() {
            @Override
            public Views mapRow(ResultSet rs, int rowNum) throws SQLException {
                Views views = new Views();
                views.setUser_id(rs.getLong("user_id"));
                views.setContent_id(rs.getLong("content_id"));
                return views;
            }
        };
    }

    // Because of composite key, override findById and deleteById if needed
    public Views findByUserAndContent(Long userId, Long contentId) {
        String sql = "SELECT * FROM " + getTableName() + " WHERE user_id = ? AND content_id = ?";
        return jdbcTemplate.queryForObject(sql, getRowMapper(), userId, contentId);
    }

    public int deleteByUserAndContent(Long userId, Long contentId) {
        String sql = "DELETE FROM " + getTableName() + " WHERE user_id = ? AND content_id = ?";
        return jdbcTemplate.update(sql, userId, contentId);
    }

    public int save(Views views) {
        String sql = "INSERT INTO " + getTableName() + " (user_id, content_id) VALUES (?, ?)";
        return jdbcTemplate.update(sql, views.getUser_id(), views.getContent_id());
    }
}