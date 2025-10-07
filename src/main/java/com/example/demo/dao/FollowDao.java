package com.example.demo.dao;

import com.example.demo.model.Follow;
import com.example.demo.model.User;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

@Repository
public class FollowDao extends BaseDao<Follow, Long> {
    private final UserDao userDao;

    public FollowDao(UserDao userDao) { // ✅ inject both
        this.userDao = userDao;
    }

    @Override
    protected String getTableName() {
        return "follows";
    }

    @Override
    protected String getIdColumn() {
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
        List<Follow> follows = jdbcTemplate.query(sql, getRowMapper(), followerId, followeeId);
        return follows.isEmpty() ? null : follows.get(0);
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

    // New methods for follow service
    public boolean isFollowing(Long followerId, Long followingId) {
        String sql = "SELECT COUNT(*) FROM " + getTableName() + " WHERE follower_id = ? AND followee_id = ?";
        Integer count = jdbcTemplate.queryForObject(sql, Integer.class, followerId, followingId);
        return count != null && count > 0;
    }

    public boolean delete(Long followerId, Long followeeId) {
        String sql = "DELETE FROM " + getTableName() + " WHERE follower_id = ? AND followee_id = ?";
        int rowsAffected = jdbcTemplate.update(sql, followerId, followeeId);
        return rowsAffected > 0;
    }

    public int getFollowerCount(Long userId) {
        String sql = "SELECT COUNT(*) FROM " + getTableName() + " WHERE followee_id = ?";
        return jdbcTemplate.queryForObject(sql, Integer.class, userId);
    }

    public int getFollowingCount(Long userId) {
        String sql = "SELECT COUNT(*) FROM " + getTableName() + " WHERE follower_id = ?";
        return jdbcTemplate.queryForObject(sql, Integer.class, userId);
    }

    // Get followers as User objects
    public List<User> getFollowers(Long userId) {
        String sql = "SELECT u.* FROM users u JOIN follows f ON u.user_id = f.follower_id WHERE f.followee_id = ?";
        return jdbcTemplate.query(sql, new UserDao(jdbcTemplate).getRowMapper(), userId);
    }

    // Get following as User objects  
    public List<User> getFollowing(Long userId) {
        String sql = "SELECT u.* FROM users u JOIN follows f ON u.user_id = f.followee_id WHERE f.follower_id = ?";
        return jdbcTemplate.query(sql, new UserDao(jdbcTemplate).getRowMapper(), userId);
    }

    public List<User> searchFollowersAndFollowees(Long userId, String prefix) {
    String sql = """
        SELECT u.*
        FROM follows f
        JOIN users u ON u.user_id = f.followee_id
        WHERE f.follower_id = ? AND u.user_name ILIKE ?
        
        UNION
        
        SELECT u.*
        FROM follows f
        JOIN users u ON u.user_id = f.follower_id
        WHERE f.followee_id = ? AND u.user_name ILIKE ?
    """;

    String likePattern = prefix + "%";

    return jdbcTemplate.query(
        sql,
        new Object[]{userId, likePattern, userId, likePattern},
        userDao.getRowMapper()
    );
}


}