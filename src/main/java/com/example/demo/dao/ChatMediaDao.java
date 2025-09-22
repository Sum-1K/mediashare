package com.example.demo.dao;

import com.example.demo.model.ChatMedia;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

@Repository
public class ChatMediaDao extends BaseDao<ChatMedia, Long> {

    @Override
    protected String getTableName() {
        return "chat_media";
    }

    @Override
    protected String getIdColumn() {
        return "chat_media_id"; // primary key is composite, so this is for simple methods
    }

    @Override
    protected RowMapper<ChatMedia> getRowMapper() {
        return new RowMapper<ChatMedia>() {
            @Override
            public ChatMedia mapRow(ResultSet rs, int rowNum) throws SQLException {
                ChatMedia media = new ChatMedia();
                media.setChat_media_id(rs.getLong("chat_media_id"));
                media.setChat_id(rs.getLong("chat_id"));
                media.setFile_type(ChatMedia.FileType.valueOf(rs.getString("file_type")));
                media.setFile_url(rs.getString("file_url"));
                return media;
            }
        };
    }

    // Insert a new media
    public int save(ChatMedia media) {
        String sql = "INSERT INTO chat_media (chat_media_id, chat_id, file_type, file_url) VALUES (?, ?, ?, ?)";
        return jdbcTemplate.update(sql,
                media.getChat_media_id(),
                media.getChat_id(),
                media.getFile_type().name(),
                media.getFile_url());
    }

    // Find all media for a specific chat
    public List<ChatMedia> findByChatId(Long chatId) {
        String sql = "SELECT * FROM chat_media WHERE chat_id = ?";
        return jdbcTemplate.query(sql, getRowMapper(), chatId);
    }

    // Optional: delete by composite key
    public int deleteById(Long chatMediaId, Long chatId) {
        String sql = "DELETE FROM chat_media WHERE chat_media_id = ? AND chat_id = ?";
        return jdbcTemplate.update(sql, chatMediaId, chatId);
    }
}
