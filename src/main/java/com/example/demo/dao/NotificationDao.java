package com.example.demo.dao;

import com.example.demo.model.Notification;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import org.springframework.dao.EmptyResultDataAccessException;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.List;

@Repository
public class NotificationDao extends BaseDao<Notification, Long> {

    @Override
    protected String getTableName() {
        return "notification";
    }

    @Override
    protected String getIdColumn() {
        return "notif_id";
    }

    @Override
    protected RowMapper<Notification> getRowMapper() {
        return new RowMapper<Notification>() {
            @Override
            public Notification mapRow(ResultSet rs, int rowNum) throws SQLException {
                return new Notification(
                        rs.getLong("notif_id"),
                        rs.getTimestamp("created_at").toLocalDateTime(),
                        rs.getString("notification_content"), // ← FIXED THIS LINE
                        rs.getLong("user_id"),
                        rs.getObject("chat_id") != null ? rs.getLong("chat_id") : null,
                        rs.getObject("request_id") != null ? rs.getLong("request_id") : null,
                        rs.getObject("comment_id") != null ? rs.getLong("comment_id") : null,
                        rs.getObject("tag_id") != null ? rs.getLong("tag_id") : null,
                        rs.getObject("like_id") != null ? rs.getLong("like_id") : null,
                        rs.getBoolean("is_read")
                );
            }
        };
    }

    public int insert(Notification notification) {
        String sql = "INSERT INTO notification (created_at, notification_content, user_id, chat_id, request_id, comment_id, tag_id, like_id) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
        return jdbcTemplate.update(sql,
                Timestamp.valueOf(notification.getCreatedAt()),
                notification.getNotificationContent(),
                notification.getUserId(),
                notification.getChatId(),
                notification.getFollowRequestId(),
                notification.getCommentId(),
                notification.getTagId(),
                notification.getLikeId());
    }

    // Update
    // public int update(Notification notification) {
    //     String sql = "UPDATE notification SET notification_content = ?, user_id = ?, chat_id = ?, request_id = ?, comment_id = ?, tag_id = ?, like_id = ?, is_read=? WHERE notif_id = ?";
    //     return jdbcTemplate.update(sql,
    //             notification.getNotificationContent(),
    //             notification.getUserId(),
    //             notification.getChatId(),
    //             notification.getFollowRequestId(),
    //             notification.getCommentId(),
    //             notification.getTagId(),
    //             notification.getLikeId(),
    //             notification.getNotifId(),
    //             notification.getIsRead())
    //             ;
    // }
    public int update(Notification notification) {
    String sql = "UPDATE notification SET notification_content = ?, user_id = ?, chat_id = ?, request_id = ?, comment_id = ?, tag_id = ?, like_id = ?, is_read = ? WHERE notif_id = ?";
    return jdbcTemplate.update(sql,
            notification.getNotificationContent(),
            notification.getUserId(),
            notification.getChatId(),
            notification.getFollowRequestId(),
            notification.getCommentId(),
            notification.getTagId(),
            notification.getLikeId(),
            notification.getIsRead(),   // <-- correct position
            notification.getNotifId()   // <-- moved to last
    );
}


    // Optional: Find by userId
    public List<Notification> findByUserId(Long userId) {
        String sql = "SELECT * FROM notification WHERE user_id = ? ORDER BY created_at DESC";
        return jdbcTemplate.query(sql, getRowMapper(), userId);
    }

    @Override
    public Notification findById(Long notifId) {
        String sql = "SELECT * FROM notification WHERE notif_id = ?";
        
        // try {
        //     return jdbcTemplate.queryForObject(sql, new Object[]{notifId}, this::mapRowToNotification);
        // } catch (EmptyResultDataAccessException e) {
        //     return null;
        // }

         try {
            return jdbcTemplate.queryForObject(sql, getRowMapper(), notifId);
        } catch (EmptyResultDataAccessException e) {
            return null;
        }
    }

    public List<Notification> findByUserIdAndIsRead(Long userId, Boolean isRead) {
        String sql = "SELECT * FROM notification WHERE user_id = ? AND is_read = ? ORDER BY created_at DESC";
        return jdbcTemplate.query(sql, getRowMapper(), userId, isRead);
    }

    public List<Notification> findUnreadByUserId(Long userId) {
        return findByUserIdAndIsRead(userId, false);
    }

    public void delete(Long notifId) {
        String sql = "DELETE FROM notification WHERE notif_id = ?";
        jdbcTemplate.update(sql, notifId);
    }

    public int countUnreadByUserId(Long userId) {
        String sql = "SELECT COUNT(*) FROM notification WHERE user_id = ? AND is_read = false";
        return jdbcTemplate.queryForObject(sql, new Object[]{userId}, Integer.class);
    }

}