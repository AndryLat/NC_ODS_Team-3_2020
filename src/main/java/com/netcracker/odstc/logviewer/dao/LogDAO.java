package com.netcracker.odstc.logviewer.dao;

import com.netcracker.odstc.logviewer.mapper.LogMapper;
import com.netcracker.odstc.logviewer.models.Log;
import org.springframework.jdbc.core.JdbcTemplate;

import java.math.BigInteger;
import java.util.Date;
import java.util.List;

public class LogDAO {


    public final JdbcTemplate jdbcTemplate;

    public LogDAO(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public List<Log> getLogByText (String text) {
        String sql = "";
        return jdbcTemplate.query(sql, new LogMapper(), text);
    }

    public List<Log> getLogByLevel (String level) {
        String sql = "";
        return jdbcTemplate.query(sql, new LogMapper(), level);
    }

    public List<Log> getLogByCreationDate (Date creationDate) {
        String sql = "";
        return jdbcTemplate.query(sql, new LogMapper(), creationDate);
    }

    public Log getById(BigInteger id) {
        String sql = "";
        return jdbcTemplate.queryForObject(sql, new LogMapper(), id);
    }

    public void save(Log log) {
        log.saveToDB();
    }

    public void deleteById(BigInteger id) {
        String sql = "DELETE FROM OBJECTS WHERE object_id = ?";
        jdbcTemplate.update(sql, id);
    }
}
