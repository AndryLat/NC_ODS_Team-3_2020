package com.netcracker.odstc.logviewer.dao;

import com.netcracker.odstc.logviewer.mapper.LogFileMapper;
import com.netcracker.odstc.logviewer.models.LogFile;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;

import java.math.BigInteger;
import java.util.Date;
import java.util.List;

public class LogFileDao {

    public final JdbcTemplate jdbcTemplate;

    @Autowired
    public LogFileDao(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public void save(LogFile logFile) {
        logFile.saveToDB();
    }

    public List<LogFile> logFiles() {
        String sql = "select ob.object_id id" +
                "from objects ob " +
                "where ob.object_type_id = 4";
        return jdbcTemplate.query(sql, new LogFileMapper());
    }

    public List<LogFile> getlogFileByName(String name) {
        String sql = "select ob.object_id id\n" +
                "from attributes NAME, objects ob " +
                "where ob.object_type_id = 4\n" +
                "and NAME.object_id = ob.object_id\n" +
                "and NAME.attr_id = 18\n" +
                "and NAME.value like ?";
        return jdbcTemplate.query(sql, new LogFileMapper(), name);
    }

    public List<LogFile> getlogFileByDate(Date date) {
        String sql = "select ob.object_id id\n" +
                "from attributes LAST_CHECK, objects ob " +
                "where ob.object_type_id = 4\n" +
                "and LAST_CHECK.object_id = ob.object_id\n" +
                "and LAST_CHECK.attr_id = 19\n" +
                "and LAST_CHECK.date_value = ?";
        return jdbcTemplate.query(sql, new LogFileMapper(), date);
    }

    public List<LogFile> getlogFileByRow(int row) {
        String sql = "select ob.object_id id\n" +
                "from attributes LAST_ROW, objects ob " +
                "where ob.object_type_id = 4\n" +
                "and LAST_ROW.object_id = ob.object_id\n" +
                "and LAST_ROW.attr_id = 20\n" +
                "and LAST_ROW.value = ?";
        return jdbcTemplate.query(sql, new LogFileMapper(), row);
    }

    public LogFile getById(BigInteger id) {
        String sql = "select ob.object_id id\n" +
                "from objects ob" +
                "where ob.object_id = ? \n" +
                "and ob.object_type_id = 4";
        return jdbcTemplate.queryForObject(sql, new LogFileMapper(), id);
    }

    public void deleteLogFile(int id) {
        String sql = "DELETE FROM OBJECTS WHERE object_id = ?";
        jdbcTemplate.update(sql, id);
    }

}
