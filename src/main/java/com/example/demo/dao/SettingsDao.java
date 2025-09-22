package com.example.demo.dao;

import com.example.demo.model.Settings;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.List;

@Repository
public class SettingsDao extends BaseDao<Settings, Long> {

    @Override
    protected String getTableName() {
        return "settings";
    }

    @Override
    protected String getIdColumn() {
        return "setting_id";
    }

    @Override
    protected RowMapper<Settings> getRowMapper() {
        return new RowMapper<Settings>() {
            @Override
            public Settings mapRow(ResultSet rs, int rowNum) throws SQLException {
                return new Settings(
                        rs.getLong("setting_id"),
                        rs.getLong("user_id"),
                        rs.getString("theme"),
                        rs.getBoolean("story_archived_enable"),
                        rs.getBoolean("allow_tags"),
                        rs.getBoolean("notification_tags"),
                        rs.getTimestamp("created_at").toLocalDateTime()
                );
            }
        };
    }

    // Insert
    public int insert(Settings settings) {
        String sql = "INSERT INTO settings (user_id, theme, story_archived_enable, allow_tags, notification_tags, created_at) VALUES (?, ?, ?, ?, ?, ?)";
        return jdbcTemplate.update(sql,
                settings.getUserId(),
                settings.getTheme(),
                settings.getStoryArchivedEnable(),
                settings.getAllowTags(),
                settings.getNotificationTags(),
                Timestamp.valueOf(settings.getCreatedAt()));
    }

    // Update
    public int update(Settings settings) {
        String sql = "UPDATE settings SET theme = ?, story_archived_enable = ?, allow_tags = ?, notification_tags = ? WHERE setting_id = ?";
        return jdbcTemplate.update(sql,
                settings.getTheme(),
                settings.getStoryArchivedEnable(),
                settings.getAllowTags(),
                settings.getNotificationTags(),
                settings.getSettingId());
    }

    // Find by userId (custom query)
    public List<Settings> findByUserId(Long userId) {
        String sql = "SELECT * FROM settings WHERE user_id = ?";
        return jdbcTemplate.query(sql, getRowMapper(), userId);
    }
}