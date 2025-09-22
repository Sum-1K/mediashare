package com.example.demo.dao;

import com.example.demo.model.Chat;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;

@Repository
public class ChatDao extends BaseDao<Chat, Long> {

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
                return chat;
            }
        };
    }
}