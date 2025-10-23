package com.example.demo.dao;

import com.example.demo.dto.ChatMediaDTO;
import com.example.demo.model.Chat;
import com.example.demo.model.ChatMedia;
import com.example.demo.model.User;
import com.example.demo.dto.ChatMessage;

import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.time.LocalDateTime;
import java.sql.Timestamp;
import java.sql.Statement;


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
    jdbcTemplate.update(connection -> {
        var ps = connection.prepareStatement(sql);
        ps.setString(1, text);
        ps.setLong(2, senderId);
        ps.setLong(3, receiverId);
        if (repliedToId != null) {
            ps.setLong(4, repliedToId);
        } else {
            ps.setNull(4, java.sql.Types.BIGINT);
        }
        return ps;
    });
    }

    public Chat saveAndReturn(Chat chat) {
        String sql = "INSERT INTO chats (seen, message, sent_at, sender_id, receiver_id, replied_to_id) VALUES (FALSE, ?, NOW(), ?, ?, ?)";

        KeyHolder keyHolder = new GeneratedKeyHolder();

        jdbcTemplate.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(sql, new String[] {"chat_id"});
            ps.setString(1, chat.getMessage());                    // ?1 -> message
            ps.setLong(2, chat.getSender_id());                    // ?2 -> sender_id
            ps.setLong(3, chat.getReceiver_id());                  // ?3 -> receiver_id
            if (chat.getReplied_to_id() != null) {
                ps.setLong(4, chat.getReplied_to_id());           // ?4 -> replied_to_id
            } else {
                ps.setNull(4, java.sql.Types.BIGINT);             // handle null replied_to_id
            }
            return ps;
        }, keyHolder);

        Number key = keyHolder.getKey();
        if (key != null) {
            chat.setChat_id(key.longValue()); // assuming your model has chatId
        }

        return chat;
    }

    public List<ChatMessage> findMessagesBetweenWithMedia(Long user1, Long user2) {
    String sql = """
        SELECT c.chat_id, c.seen, c.message, c.sent_at, c.sender_id, c.receiver_id, c.replied_to_id, m.file_url, m.file_type
        FROM chats c
        LEFT JOIN chat_media m ON m.chat_id = c.chat_id
        WHERE (c.sender_id = ? AND c.receiver_id = ?)
           OR (c.sender_id = ? AND c.receiver_id = ?)
        ORDER BY c.sent_at ASC
    """;

    return jdbcTemplate.query(sql, new Object[]{user1, user2, user2, user1}, (rs, rowNum) -> {
        ChatMessage msg = new ChatMessage();
        msg.setSenderId(rs.getLong("sender_id"));
        msg.setReceiverId(rs.getLong("receiver_id"));

        // Use the column name 'message' (selected as c.message)
        msg.setContent(rs.getString("message"));
        Timestamp ts = rs.getTimestamp("sent_at");
        if (ts != null) {
            msg.setSentAt(ts.toLocalDateTime());
        }

        long repliedId = rs.getLong("replied_to_id");
        msg.setRepliedToId(rs.wasNull() ? null : repliedId);

        msg.setChatId(rs.getLong("chat_id"));

        String fileUrl = rs.getString("file_url");
        if (fileUrl != null) {
            ChatMediaDTO media = new ChatMediaDTO();
            media.setFileUrl(fileUrl); // adjust as needed for your server
            String fileTypeStr = rs.getString("file_type");
            if (fileTypeStr != null) {
                // normalize to enum name (avoid case mismatch)
                media.setFileType(ChatMedia.FileType.valueOf(fileTypeStr.toUpperCase()));
            }
            msg.setMedia(media);
        }

        return msg;
    }); // <-- close jdbcTemplate.query call
}

}