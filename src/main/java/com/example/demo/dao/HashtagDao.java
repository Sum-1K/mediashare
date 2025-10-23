package com.example.demo.dao;

import com.example.demo.model.Hashtag;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

@Repository
public class HashtagDao extends BaseDao<Hashtag, Long> {

    @Override
    protected String getTableName() {
        return "hashtags";
    }

    @Override
    protected String getIdColumn() {
        return "hashtag_id";
    }

    @Override
    protected RowMapper<Hashtag> getRowMapper() {
        return new RowMapper<Hashtag>() {
            @Override
            public Hashtag mapRow(ResultSet rs, int rowNum) throws SQLException {
                return new Hashtag(
                        rs.getLong("hashtag_id"),
                        rs.getString("text")
                );
            }
        };
    }

    // Insert
    public int insert(Hashtag hashtag) {
        String sql = "INSERT INTO hashtags (text) VALUES (?)";
        return jdbcTemplate.update(sql, hashtag.getText());
    }

    // Update
    public int update(Hashtag hashtag) {
        String sql = "UPDATE hashtags SET text = ? WHERE hashtag_id = ?";
        return jdbcTemplate.update(sql, hashtag.getText(), hashtag.getHashtagId());
    }

    // Optional: find by text
    public Hashtag findByText(String text) {
        String sql = "SELECT * FROM hashtags WHERE text = ?";
        return jdbcTemplate.queryForObject(sql, getRowMapper(), text);
    }

    public List<Hashtag> searchByText(String query) {
        String sql = "SELECT * FROM hashtags WHERE text ILIKE ? ORDER BY text ASC LIMIT 10";
        return jdbcTemplate.query(sql, getRowMapper(), "%" + query + "%");
    }

}