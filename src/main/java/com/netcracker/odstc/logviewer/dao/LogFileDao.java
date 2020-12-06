package com.netcracker.odstc.logviewer.dao;

import com.netcracker.odstc.logviewer.mapper.LogFileMapper;
import com.netcracker.odstc.logviewer.models.LogFile;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;

import java.util.Date;
import java.util.List;

public class LogFileDao {

    public final JdbcTemplate jdbcTemplate;

    @Autowired
    public LogFileDao(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public List<LogFile> logFiles() {
        String sql = "select ob.object_id id, NAME.value NAME, LAST_CHECK.date_value LAST_CHECK, LAST_ROW.value LAST_ROW\n" +
                "from attributes NAME, attributes LAST_CHECK, attributes LAST_ROW, objects ob " +
                "where ob.object_type_id = 4\n" +
                "and NAME.object_id = ob.object_id\n" +
                "and NAME.attr_id = 18\n" +
                "and LAST_CHECK.object_id = ob.object_id\n" +
                "and LAST_CHECK.attr_id = 19\n" +
                "and LAST_ROW.object_id = ob.object_id\n" +
                "and LAST_ROW.attr_id = 20\n";
        return jdbcTemplate.query(sql, new LogFileMapper());
    }

    public List<LogFile> getlogFileByName(String name) {
        String sql = "select ob.object_id id, NAME.value NAME, LAST_CHECK.date_value LAST_CHECK, LAST_ROW.value LAST_ROW\n" +
                "from attributes NAME, attributes LAST_CHECK, attributes LAST_ROW, objects ob " +
                "where ob.object_type_id = 4\n" +
                "and NAME.object_id = ob.object_id\n" +
                "and NAME.attr_id = 18\n" +
                "and NAME.value = ?\n" +
                "and LAST_CHECK.object_id = ob.object_id\n" +
                "and LAST_CHECK.attr_id = 19\n" +
                "and LAST_ROW.object_id = ob.object_id\n" +
                "and LAST_ROW.attr_id = 20\n";
        return jdbcTemplate.query(sql, new LogFileMapper(), name);
    }

    public List<LogFile> getlogFileByDate(Date date) {
        String sql = "select ob.object_id id, NAME.value NAME, LAST_CHECK.date_value LAST_CHECK, LAST_ROW.value LAST_ROW\n" +
                "from attributes NAME, attributes LAST_CHECK, attributes LAST_ROW, objects ob " +
                "where ob.object_type_id = 4\n" +
                "and NAME.object_id = ob.object_id\n" +
                "and NAME.attr_id = 18\n" +
                "and LAST_CHECK.object_id = ob.object_id\n" +
                "and LAST_CHECK.attr_id = 19\n" +
                "and LAST_CHECK.date_value = ?\n" +
                "and LAST_ROW.object_id = ob.object_id\n" +
                "and LAST_ROW.attr_id = 20\n";
        return jdbcTemplate.query(sql, new LogFileMapper(), date);
    }

    public List<LogFile> getlogFileByRow(int row) {
        String sql = "select ob.object_id id, NAME.value NAME, LAST_CHECK.date_value LAST_CHECK, LAST_ROW.value LAST_ROW\n" +
                "from attributes NAME, attributes LAST_CHECK, attributes LAST_ROW, objects ob " +
                "where ob.object_type_id = 4\n" +
                "and NAME.object_id = ob.object_id\n" +
                "and NAME.attr_id = 18\n" +
                "and LAST_CHECK.object_id = ob.object_id\n" +
                "and LAST_CHECK.attr_id = 19\n" +
                "and LAST_ROW.object_id = ob.object_id\n" +
                "and LAST_ROW.attr_id = 20\n" +
                "and LAST_ROW.value = ?\n";
        return jdbcTemplate.query(sql, new LogFileMapper(), row);
    }

    public LogFile getById(int id) {
        String sql = "select ob.object_id id, NAME.value NAME, LAST_CHECK.date_value LAST_CHECK, LAST_ROW.value LAST_ROW\n" +
                "from attributes NAME, attributes login, attributes LAST_CHECK, attributes LAST_ROW, objects ob" +
                "where ob.object_id = ? \n" +
                "and NAME.object_id = ob.object_id\n" +
                "and NAME.attr_id = 18\n" +
                "and LAST_CHECK.object_id = ob.object_id\n" +
                "and LAST_CHECK.attr_id = 19\n" +
                "and LAST_ROW.object_id = ob.object_id\n" +
                "and LAST_ROW.attr_id = 20\n";
        return jdbcTemplate.queryForObject(sql, new LogFileMapper(), id);
    }

    public void addLogFile(String NAME, String fileNAME, Date LAST_CHECK, int LAST_ROW) {
        String sql = "INSERT ALL\n" +
                "INTO OBJECTS(OBJECT_ID, OBJECT_TYPE_ID, NAME)\n" +
                "VALUES(OBJECT_ID_seq.nextval, 4, ?)\n" +
                "INTO ATTRIBUTES(attr_id, object_id, value)\n" +
                "VALUES(18, OBJECT_ID_seq.currval, ?)\n" +
                "INTO ATTRIBUTES(attr_id, object_id, date_value)\n" +
                "VALUES(19, OBJECT_ID_seq.currval, ?)\n" +
                "INTO ATTRIBUTES(attr_id, object_id, value)\n" +
                "VALUES(20, OBJECT_ID_seq.currval, ?)\n" +
                "SELECT * FROM dual";
        jdbcTemplate.update(sql, NAME, fileNAME, LAST_CHECK, LAST_ROW);
    }

    public void deleteLogFile(int id) {
        String sql = "DELETE FROM OBJECTS WHERE object_id = ?";
        jdbcTemplate.update(sql, id);
    }

}
