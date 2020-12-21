package com.netcracker.odstc.logviewer.dao;

import com.netcracker.odstc.logviewer.mapper.LogMapper;
import com.netcracker.odstc.logviewer.models.Log;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.math.BigInteger;
import java.util.Date;
import java.util.List;

@Repository
public class LogDAO extends EAVObjectDAO {

    public LogDAO(JdbcTemplate jdbcTemplate) {
        super(jdbcTemplate);
    }

    public Log getById(BigInteger id) {
        Log log = getObjectById(id, Log.class);
        return log;
    }

    public List<Log> getLogByText(String text) {
        String sql = "select \n" +
                "       fcl.value          as fcl_value\n" +
                "from objects ob\n" +
                "left join attributes fcl on fcl.object_id = ob.object_id\n" +
                "left join attributes ll on ll.object_id = ob.object_id\n" +
                "left join Lists lll on lll.attr_id = 22 /* Log level */ and lll.list_value_id = ll.list_value_id\n" +
                "left join attributes lt on lt.object_id = ob.object_id\n" +
                "where fcl.value  = ?\n" +
                "and ob.object_type_id = 5\n" +
                "and fcl.attr_id = 21 /* Full content of log */\n" +
                "and ll.attr_id = 22 /* Log level */\n" +
                "and lt.attr_id = 23 /* Log timestamp */;";
        return jdbcTemplate.query(sql, new LogMapper(), text);
    }

    public List<Log> getLogByLevel(String level) {
        String sql = "select lll.value          as log_level_value\n" +
                "from objects ob\n" +
                "left join attributes fcl on fcl.object_id = ob.object_id\n" +
                "left join attributes ll on ll.object_id = ob.object_id\n" +
                "left join Lists lll on lll.attr_id = 22 /* Log level */ and lll.list_value_id = ll.list_value_id\n" +
                "left join attributes lt on lt.object_id = ob.object_id\n" +
                "where lll.value = ?\n" +
                "and ob.object_type_id = 5\n" +
                "and fcl.attr_id = 21 /* Full content of log */\n" +
                "and ll.attr_id = 22 /* Log level */\n" +
                "and lt.attr_id = 23 /* Log timestamp */;   ";
        return jdbcTemplate.query(sql, new LogMapper(), level);
    }

    public List<Log> getLogByCreationDate(Date creationDate) {
        String sql = "select lt.date_value as  log_timestamp_value\n" +
                "from objects ob\n" +
                "left join attributes fcl on fcl.object_id = ob.object_id\n" +
                "left join attributes ll on ll.object_id = ob.object_id\n" +
                "left join Lists lll on lll.attr_id = 22 /* Log level */ and lll.list_value_id = ll.list_value_id\n" +
                "left join attributes lt on lt.object_id = ob.object_id\n" +
                "where lt.date_value = ?\n" +
                "and ob.object_type_id = 5\n" +
                "and fcl.attr_id = 21 /* Full content of log */\n" +
                "and ll.attr_id = 22 /* Log level */\n" +
                "and lt.attr_id = 23 /* Log timestamp */;";
        return jdbcTemplate.query(sql, new LogMapper(), creationDate);
    }

    public List<Log> getAll() {
        String sql = "select ob.object_id       as id, \n" +
                "       fcl.value          as fcl_value,\n" +
                "       lll.value          as log_level_value,\n" +
                "       lt.date_value as  log_timestamp_value\n" +
                "from objects ob\n" +
                "left join attributes fcl on fcl.object_id = ob.object_id\n" +
                "left join attributes ll on ll.object_id = ob.object_id\n" +
                "left join Lists lll on lll.attr_id = 22 /* Log level */ and lll.list_value_id = ll.list_value_id\n" +
                "left join attributes lt on lt.object_id = ob.object_id\n" +
                "where ob.object_type_id = 5\n" +
                "and fcl.attr_id = 21 /* Full content of log */\n" +
                "and ll.attr_id = 22 /* Log level */\n" +
                "and lt.attr_id = 23 /* Log timestamp */\n" +
                ";";
        return jdbcTemplate.query(sql, new LogMapper());
    }

    public void save(Log log) {
        super.saveObject(log);
    }

    public void update(Log log) {
        saveObject(log);
        saveAttributes(log.getObjectId(), log.getAttributes());
        saveReferences(log.getObjectId(), log.getReferences());
    }

    public void deleteById(BigInteger id) {
        String sql = "DELETE FROM OBJECTS WHERE object_id = ?";
        jdbcTemplate.update(sql, id);
    }
}
