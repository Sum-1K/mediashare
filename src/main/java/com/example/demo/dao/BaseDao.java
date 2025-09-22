package com.example.demo.dao;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

public abstract class BaseDao<T, ID> {

    @Autowired
    protected JdbcTemplate jdbcTemplate;

    protected abstract String getTableName();
    protected abstract String getIdColumn();
    protected abstract RowMapper<T> getRowMapper();

    public List<T> findAll() {
        String sql = "SELECT * FROM " + getTableName();
        return jdbcTemplate.query(sql, getRowMapper());
    }

    public T findById(ID id) {
    String sql = "SELECT * FROM " + getTableName() + " WHERE id = ?";
    return jdbcTemplate.queryForObject(sql, getRowMapper(), id);
}

    public int deleteById(ID id) {
        String sql = "DELETE FROM " + getTableName() + " WHERE id = ?";
        return jdbcTemplate.update(sql, id);
    }

    // you can add generic insert/update later, if you can build queries dynamically
}