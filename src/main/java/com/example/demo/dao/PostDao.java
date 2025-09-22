package com.example.demo.dao;

import com.example.demo.model.Post;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;

@Repository
public class PostDao extends BaseDao<Post, Long> {

    private final RowMapper<Post> postRowMapper = new RowMapper<>() {
        @Override
        public Post mapRow(ResultSet rs, int rowNum) throws SQLException {
            Post post = new Post();
            post.setPostId(rs.getLong("post_id"));
            post.setCaption(rs.getString("caption"));
            return post;
        }
    };

    @Override
    protected String getTableName() {
        return "posts";
    }

    @Override
    protected String getIdColumn() {
        return "post_id";
    }

    @Override
    protected RowMapper<Post> getRowMapper() {
        return postRowMapper;
    }

    // Create a post (requires content_id already generated)
    public int save(Post post) {
        String sql = "INSERT INTO posts(post_id, caption) VALUES(?, ?)";
        return jdbcTemplate.update(sql, post.getPostId(), post.getCaption());
    }

    // Update post
    public int update(Post post) {
        String sql = "UPDATE posts SET caption = ? WHERE post_id = ?";
        return jdbcTemplate.update(sql, post.getCaption(), post.getPostId());
    }
}
