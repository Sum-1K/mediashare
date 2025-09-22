package com.example.demo.dao;

import com.example.demo.model.FollowRequest;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;

@Repository
public class FollowRequestDao extends BaseDao<FollowRequest, Long> {

    @Override
    protected String getTableName() {
        return "follow_requests";
    }

    @Override
    protected String getIdColumn() {
        return "request_id";
    }

    @Override
    protected RowMapper<FollowRequest> getRowMapper() {
        return new RowMapper<FollowRequest>() {
            @Override
            public FollowRequest mapRow(ResultSet rs, int rowNum) throws SQLException {
                FollowRequest request = new FollowRequest();
                request.setRequest_id(rs.getLong("request_id"));
                request.setSender_id(rs.getLong("sender_id"));
                request.setReceiver_id(rs.getLong("receiver_id"));
                request.setStatus(rs.getString("status"));
                request.setCreated_at(rs.getTimestamp("created_at").toLocalDateTime());
                return request;
            }
        };
    }
}