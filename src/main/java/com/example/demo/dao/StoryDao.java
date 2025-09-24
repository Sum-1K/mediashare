package com.example.demo.dao;

import com.example.demo.model.Story;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import java.util.List;

import java.sql.ResultSet;
import java.sql.SQLException;

@Repository
public class StoryDao extends BaseDao<Story, Long> {

    private final RowMapper<Story> storyRowMapper = new RowMapper<>() {
        @Override
        public Story mapRow(ResultSet rs, int rowNum) throws SQLException {
            Story story = new Story();
            story.setStoryId(rs.getLong("story_id"));
            story.setMediaFile(rs.getString("media_file"));
            story.setHighlightTopic(rs.getString("highlight_topic"));
            story.setIsHighlighted(rs.getBoolean("is_highlighted"));
            story.setIsArchived(rs.getBoolean("is_archived"));
            return story;
        }
    };

    @Override
    protected String getTableName() {
        return "stories";
    }

    @Override
    protected String getIdColumn() {
        return "story_id";
    }

    @Override
    protected RowMapper<Story> getRowMapper() {
        return storyRowMapper;
    }

    public int save(Story story) {
        String sql = "INSERT INTO stories(story_id, media_file, highlight_topic, is_highlighted, is_archived) VALUES (?, ?, ?, ?, ?)";
        return jdbcTemplate.update(sql,
                story.getStoryId(),
                story.getMediaFile(),
                story.getHighlightTopic(),
                story.getIsHighlighted(),
                story.getIsArchived());
    }
    
    public List<Story> findActiveStories() {
    // Select stories joined with content, but only last 24 hours
    String sql = "SELECT s.* FROM stories s " +
                 "JOIN content c ON s.story_id = c.content_id " +
                 "WHERE c.created_at >= NOW() - INTERVAL '24 hours' " +
                 "ORDER BY c.created_at DESC";

    return jdbcTemplate.query(sql, getRowMapper());
    }


    public int update(Story story) {
        String sql = "UPDATE stories SET media_file=?, highlight_topic=?, is_highlighted=?, is_archived=? WHERE story_id=?";
        return jdbcTemplate.update(sql,
                story.getMediaFile(),
                story.getHighlightTopic(),
                story.getIsHighlighted(),
                story.getIsArchived(),
                story.getStoryId());
    }
}
