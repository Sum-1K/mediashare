package com.example.demo.dao;

import com.example.demo.model.Chat;
import com.example.demo.model.User;

import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

@Repository
public class ChatDao extends BaseDao<Chat, Long> {
    private final UserDao userDao;

    public ChatDao(UserDao userDao) { // ✅ inject both
        this.userDao = userDao;
    }

    @Override
    protected String getTableName() {
        return "chats";
    }

    @Override
    protected String getIdColumn() {
        return "chat_id";
    }

    @Override
    protected RowMapper<Chat> getRowMapper() {
        return new RowMapper<Chat>() {
            @Override
            public Chat mapRow(ResultSet rs, int rowNum) throws SQLException {
                Chat chat = new Chat();
                chat.setChat_id(rs.getLong("chat_id"));
                chat.setSeen(rs.getBoolean("seen"));
                chat.setMessage(rs.getString("message"));
                chat.setSent_at(rs.getTimestamp("sent_at").toLocalDateTime());
                chat.setSender_id(rs.getLong("sender_id"));
                chat.setReceiver_id(rs.getLong("receiver_id"));
                chat.setReplied_to_id(rs.getLong("replied_to_id"));
                return chat;
            }
        };
    }

    public List<User> findChatUsers(Long currentUserId) {
        String sql = """
            SELECT DISTINCT u.*
            FROM users u
            WHERE u.user_id IN (
                SELECT CASE 
                    WHEN sender_id = ? THEN receiver_id
                    ELSE sender_id 
                END
                FROM chats
                WHERE sender_id = ? OR receiver_id = ?
            )
        """;

        return jdbcTemplate.query(sql, new Object[]{currentUserId, currentUserId, currentUserId}, userDao.getRowMapper());
    }

    public List<Chat> findMessagesBetween(Long user1, Long user2) {
    String sql = """
        SELECT * FROM chats
        WHERE (sender_id = ? AND receiver_id = ?)
           OR (sender_id = ? AND receiver_id = ?)
        ORDER BY sent_at ASC
    """;
    return jdbcTemplate.query(sql, getRowMapper(), user1, user2, user2, user1);
    }

    public void saveMessage(Long senderId, Long receiverId, String text, Long repliedToId) {
    String sql = "INSERT INTO chats (seen, message, sent_at, sender_id, receiver_id, replied_to_id) VALUES (FALSE, ?, NOW(), ?, ?, ?)";
    jdbcTemplate.update(sql, text, senderId, receiverId, repliedToId);
    }
}