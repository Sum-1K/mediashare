package com.example.demo.dao;

import com.example.demo.model.BlockedUser;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.sql.Timestamp;


@Repository
public class BlockedUserDao extends BaseDao<BlockedUser, Long> {

    @Override
    protected String getTableName() {
        return "blocked_users";
    }

    @Override
    protected String getIdColumn() {
        // Not useful here, but required by BaseDao
        return "blocker_by_id";
    }

    @Override
    protected RowMapper<BlockedUser> getRowMapper() {
        return new RowMapper<BlockedUser>() {
            @Override
            public BlockedUser mapRow(ResultSet rs, int rowNum) throws SQLException {
                BlockedUser blockedUser = new BlockedUser();
                blockedUser.setBlockerById(rs.getLong("blocker_by_id"));
                blockedUser.setBlockedToId(rs.getLong("blocked_to_id"));
                // ✅ Handle null safely
            Timestamp timestamp = rs.getTimestamp("since");
            if (timestamp != null) {
                blockedUser.setSince(timestamp.toLocalDateTime());
            } else {
                blockedUser.setSince(null);
            }
                return blockedUser;
            }
        };
    }

    // ✅ Save new blocked user record
    public int save(BlockedUser blockedUser) {
        String sql = "INSERT INTO " + getTableName() +
                     " (blocker_by_id, blocked_to_id, since) VALUES (?, ?, ?)";
        return jdbcTemplate.update(sql,
                blockedUser.getBlockerById(),
                blockedUser.getBlockedToId(),
                blockedUser.getSince());
    }

    // ✅ Find by composite key
    public BlockedUser findById(Long blockerById, Long blockedToId) {
        String sql = "SELECT * FROM " + getTableName() + " WHERE blocker_by_id = ? AND blocked_to_id = ?";
        return jdbcTemplate.queryForObject(sql, getRowMapper(), blockerById, blockedToId);
    }

    // ✅ Delete by composite key
    public int deleteById(Long blockerById, Long blockedToId) {
        String sql = "DELETE FROM " + getTableName() + " WHERE blocker_by_id = ? AND blocked_to_id = ?";
        return jdbcTemplate.update(sql, blockerById, blockedToId);
    }

    // ✅ Get all blocked users of a given blocker
    public List<BlockedUser> findAllByBlocker(Long blockerById) {
        String sql = "SELECT * FROM " + getTableName() + " WHERE blocker_by_id = ?";
        return jdbcTemplate.query(sql, getRowMapper(), blockerById);
    }

    // ✅ Get all who blocked a given user
    public List<BlockedUser> findAllByBlocked(Long blockedToId) {
        String sql = "SELECT * FROM " + getTableName() + " WHERE blocked_to_id = ?";
        return jdbcTemplate.query(sql, getRowMapper(), blockedToId);
    }

    public boolean exists(Long blockerById, Long blockedToId) {
    String sql = "SELECT COUNT(*) FROM " + getTableName() + 
                 " WHERE blocker_by_id = ? AND blocked_to_id = ?";
    Integer count = jdbcTemplate.queryForObject(sql, Integer.class, blockerById, blockedToId);
    return count != null && count > 0;
}

public boolean isBlocked(Long blockerId, Long blockedId) {
    String sql = "SELECT COUNT(*) FROM blocked_users WHERE blocker_by_id = ? AND blocked_to_id = ?";
    Integer count = jdbcTemplate.queryForObject(sql, Integer.class, blockerId, blockedId);
    return count != null && count > 0;
}


}