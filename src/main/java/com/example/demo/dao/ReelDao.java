package com.example.demo.dao;

import com.example.demo.model.Reel;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

@Repository
public class ReelDao extends BaseDao<Reel, Long> {

    private final RowMapper<Reel> reelRowMapper = new RowMapper<>() {
        @Override
        public Reel mapRow(ResultSet rs, int rowNum) throws SQLException {
            Reel reel = new Reel();
            reel.setReelId(rs.getLong("reel_id"));
            reel.setCaption(rs.getString("caption"));
            reel.setVideoFile(rs.getString("video_file"));
            return reel;
        }
    };

    @Override
    protected String getTableName() {
        return "reels";
    }

    @Override
    protected String getIdColumn() {
        return "reel_id";
    }

    @Override
    protected RowMapper<Reel> getRowMapper() {
        return reelRowMapper;
    }

    public int save(Reel reel) {
        String sql = "INSERT INTO reels(reel_id, caption, video_file) VALUES (?, ?, ?)";
        return jdbcTemplate.update(sql,
                reel.getReelId(),
                reel.getCaption(),
                reel.getVideoFile());
    }

    public int update(Reel reel) {
        String sql = "UPDATE reels SET caption=?, video_file=? WHERE reel_id=?";
        return jdbcTemplate.update(sql,
                reel.getCaption(),
                reel.getVideoFile(),
                reel.getReelId());
    }

    // Count reels by userId
public int countByUserId(Long userId) {
    String sql = "SELECT COUNT(*) FROM reels r " +
                 "JOIN content c ON r.reel_id = c.content_id " +
                 "WHERE c.user_id = ?";
    return jdbcTemplate.queryForObject(sql, Integer.class, userId);
}


// Find reels by userId
public List<Reel> findByUserId(Long userId) {
    String sql = "SELECT r.* FROM reels r " +
                 "JOIN content c ON r.reel_id = c.content_id " +
                 "WHERE c.user_id = ? ORDER BY c.created_at DESC";
    return jdbcTemplate.query(sql, getRowMapper(), userId);
}
}
