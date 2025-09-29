package com.example.demo.dao;

import com.example.demo.model.Follow;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

@Repository
public class FollowDao extends BaseDao<Follow, Long> {

    @Override
    protected String getTableName() {
        return "follows";
    }

    @Override
    protected String getIdColumn() {
        // Not really useful because we have a composite key,
        // but BaseDao requires it, so we just return one of them.
        return "follower_id";
    }

    @Override
    protected RowMapper<Follow> getRowMapper() {
        return new RowMapper<Follow>() {
            @Override
            public Follow mapRow(ResultSet rs, int rowNum) throws SQLException {
                Follow follow = new Follow();
                follow.setFollowerId(rs.getLong("follower_id"));
                follow.setFolloweeId(rs.getLong("followee_id"));
                follow.setCloseFriend(rs.getBoolean("is_close_friend"));
                follow.setSince(rs.getTimestamp("since").toLocalDateTime());
                return follow;
            }
        };
    }

    // ✅ Override: findById using composite key
    public Follow findById(Long followerId, Long followeeId) {
        String sql = "SELECT * FROM " + getTableName() + " WHERE follower_id = ? AND followee_id = ?";
        return jdbcTemplate.queryForObject(sql, getRowMapper(), followerId, followeeId);
    }

    // ✅ Override: deleteById using composite key
    public int deleteById(Long followerId, Long followeeId) {
        String sql = "DELETE FROM " + getTableName() + " WHERE follower_id = ? AND followee_id = ?";
        return jdbcTemplate.update(sql, followerId, followeeId);
    }

    // ✅ Save new follow relationship
    public int save(Follow follow) {
        String sql = "INSERT INTO " + getTableName() +
                     " (follower_id, followee_id, is_close_friend, since) VALUES (?, ?, ?, ?)";
        return jdbcTemplate.update(sql,
                follow.getFollowerId(),
                follow.getFolloweeId(),
                follow.isCloseFriend(),
                follow.getSince());
    }

    // ✅ Get all followees of a given user
    public List<Follow> findAllByFollower(Long followerId) {
        String sql = "SELECT * FROM " + getTableName() + " WHERE follower_id = ?";
        return jdbcTemplate.query(sql, getRowMapper(), followerId);
    }

    // ✅ Get all followers of a given user
    public List<Follow> findAllByFollowee(Long followeeId) {
        String sql = "SELECT * FROM " + getTableName() + " WHERE followee_id = ?";
        return jdbcTemplate.query(sql, getRowMapper(), followeeId);
    }

    public int countFollowers(Long userId) {
        String sql = "SELECT COUNT(*) FROM follows WHERE followee_id = ?";
        return jdbcTemplate.queryForObject(sql, Integer.class, userId);
    }

    public int countFollowing(Long userId) {
        String sql = "SELECT COUNT(*) FROM follows WHERE follower_id = ?";
        return jdbcTemplate.queryForObject(sql, Integer.class, userId);
    }

}