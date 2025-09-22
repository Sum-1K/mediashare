package com.example.demo.dao;

import com.example.demo.model.User;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import java.sql.*;
import java.util.List;
import java.util.Optional;

@Repository
public class UserDao extends BaseDao<User, Long> {

    // RowMapper for User
    private final RowMapper<User> rowMapper = new RowMapper<>() {
        @Override
        public User mapRow(ResultSet rs, int rowNum) throws SQLException {
            User user = new User();
            user.setUser_id(rs.getLong("user_id"));
            user.setUser_name(rs.getString("user_name"));
            user.setFirst_name(rs.getString("first_name"));
            user.setLast_name(rs.getString("last_name"));
            if (rs.getDate("dob") != null) user.setDob(rs.getDate("dob").toLocalDate());
            user.setEmail(rs.getString("email"));
            user.setBio(rs.getString("bio"));
            String privacyStr = rs.getString("privacy");
            user.setPrivacy(privacyStr != null ? User.Privacy.valueOf(privacyStr) : null);
            user.setPhoto(rs.getString("photo"));
            if (rs.getTimestamp("join_date") != null)
                user.setJoin_date(rs.getTimestamp("join_date").toLocalDateTime());
            return user;
        }
    };

    public UserDao(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    // Abstract method implementations
    @Override
    protected String getTableName() {
        return "users";
    }

    @Override
    protected String getIdColumn() {
        return "user_id";
    }

    @Override
    protected RowMapper<User> getRowMapper() {
        return rowMapper;
    }

    // Custom save method (returns generated id)
    public Long save(User user) {
        final String sql = "INSERT INTO users (user_name, first_name, last_name, dob, email, bio, privacy, photo, join_date) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
        KeyHolder keyHolder = new GeneratedKeyHolder();

        jdbcTemplate.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            ps.setString(1, user.getUser_name());
            ps.setString(2, user.getFirst_name());
            ps.setString(3, user.getLast_name());
            if (user.getDob() != null) ps.setDate(4, Date.valueOf(user.getDob()));
            else ps.setNull(4, Types.DATE);
            ps.setString(5, user.getEmail());
            ps.setString(6, user.getBio());
            ps.setString(7, user.getPrivacy() == null ? null : user.getPrivacy().name());
            ps.setString(8, user.getPhoto());
            if (user.getJoin_date() != null)
                ps.setTimestamp(9, Timestamp.valueOf(user.getJoin_date()));
            else
                ps.setTimestamp(9, new Timestamp(System.currentTimeMillis()));
            return ps;
        }, keyHolder);

        Number key = keyHolder.getKey();
        return key != null ? key.longValue() : null;
    }

    // Custom update method
    public int update(User user) {
        final String sql = "UPDATE users SET user_name=?, first_name=?, last_name=?, dob=?, email=?, bio=?, privacy=?, photo=? " +
                "WHERE user_id=?";
        return jdbcTemplate.update(sql,
                user.getUser_name(),
                user.getFirst_name(),
                user.getLast_name(),
                user.getDob() != null ? Date.valueOf(user.getDob()) : null,
                user.getEmail(),
                user.getBio(),
                user.getPrivacy() != null ? user.getPrivacy().name() : null,
                user.getPhoto(),
                user.getUser_id());
    }

    // Find by username
    public Optional<User> findByUserName(String username) {
        final String sql = "SELECT * FROM users WHERE user_name = ?";
        List<User> list = jdbcTemplate.query(sql, rowMapper, username);
        return list.isEmpty() ? Optional.empty() : Optional.of(list.get(0));
    }

    // Exists by email
    public boolean existsByEmail(String email) {
        final String sql = "SELECT count(1) FROM users WHERE email = ?";
        Integer count = jdbcTemplate.queryForObject(sql, Integer.class, email);
        return count != null && count > 0;
    }
}
