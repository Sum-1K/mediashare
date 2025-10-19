package com.example.demo.dao;

import com.example.demo.model.Media;
import com.example.demo.model.Media.MediaType;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

@Repository
public class MediaDao extends BaseDao<Media, Long> {

    @Override
    protected String getTableName() {
        return "media";
    }

    @Override
    protected String getIdColumn() {
        return "media_id";
    }

    @Override
    protected RowMapper<Media> getRowMapper() {
        return new RowMapper<Media>() {
            @Override
            public Media mapRow(ResultSet rs, int rowNum) throws SQLException {
                return new Media(
                        rs.getLong("media_id"),
                        MediaType.valueOf(rs.getString("type")),
                        rs.getString("url"),
                        rs.getLong("post_id"),
                        rs.getObject("media_order") != null ? rs.getInt("media_order") : null
                );
            }
        };
    }

    public int insert(Media media) {
        String sql = "INSERT INTO media (type, url, post_id, media_order) VALUES (?, ?, ?, ?)";
        return jdbcTemplate.update(sql,
                media.getType().name(),
                media.getUrl(),
                media.getPostId(),
                media.getMediaOrder());
    }

    public int update(Media media) {
        String sql = "UPDATE media SET type = ?, url = ?, post_id = ?, media_order = ? WHERE media_id = ?";
        return jdbcTemplate.update(sql,
                media.getType().name(),
                media.getUrl(),
                media.getPostId(),
                media.getMediaOrder(),
                media.getMediaId());
    }

    public List<Media> findByPostId(Long postId) {
        String sql = "SELECT * FROM media WHERE post_id = ?";
        return jdbcTemplate.query(sql, getRowMapper(), postId);
    }
}