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

    public List<Log> getLogByAll(String text, Date dat1, Date dat2, int V_SEVERE, int V_WARNING,
                                  int V_INFO,int V_CONFIG,int V_FINE,int V_FINER,int V_FINEST,int V_DEBUG,
                                  int V_TRACE,int V_ERROR,int V_FATAL, int V_SORT) {
        String sql = "select ob.object_id       as id, \n" +
                "                       fcl.value          as fcl_value,\n" +
                "                       lll.value          as log_level_value,\n" +
                "                       lt.date_value as  log_timestamp_value\n" +
                "                from objects ob\n" +
                "                left join attributes fcl on fcl.object_id = ob.object_id\n" +
                "                left join attributes ll on ll.object_id = ob.object_id\n" +
                "                left join Lists lll on lll.attr_id = 22 /* Log level */ and lll.list_value_id = ll.list_value_id\n" +
                "                left join attributes lt on lt.object_id = ob.object_id\n" +
                "                where ob.object_type_id = 5\n" +
                "                and fcl.attr_id = 21 /* Full content of log */\n" +
                "                and ll.attr_id = 22 /* Log level */\n" +
                "                and lt.attr_id = 23 /* Log timestamp */\n" +
                "\t\t\t\t\n" +
                "\t\t\t\tand fcl.value like '%<text>%'\n" +
                "\t\t\t\tand lt.date_value between nvl(<dat1>, lt.date_value) and nvl(<dat2>, lt.date_value)\n" +
                "\t\t\t\tand \n" +
                "\t\t\t\t    (\n" +
                "\t\t\t\t\t  (\n" +
                "\t\t\t\t        <V_SEVERE> + <V_WARNING> + <V_INFO> + <V_CONFIG> + <V_FINE> + <V_FINER>\n" +
                "                        + <V_FINEST> + <V_DEBUG> + <V_TRACE> + <V_ERROR> + <V_FATAL> = 0\n" +
                "\t\t\t\t      )\n" +
                "\t\t\t\t\t or\n" +
                "\t\t\t\t\t  (\n" +
                "\t\t\t\t\t      (<V_SEVERE> = 1 and lll.value = 'SEVERE')\n" +
                "\t\t\t\t\t   or (<V_WARNING> = 1 and lll.value = 'WARNING')\n" +
                "\t\t\t\t\t   or (<V_INFO> = 1 and lll.value = 'INFO')\n" +
                "\t\t\t\t\t   or (<V_CONFIG> = 1 and lll.value = 'CONFIG')\n" +
                "\t\t\t\t\t   or (<V_FINE> = 1 and lll.value = 'FINE')\n" +
                "\t\t\t\t\t   or (<V_FINER> = 1 and lll.value = 'FINER')\n" +
                "\t\t\t\t\t   or (<V_FINEST> = 1 and lll.value = 'FINEST')\n" +
                "\t\t\t\t\t   or (<V_DEBUG> = 1 and lll.value = 'DEBUG')\n" +
                "\t\t\t\t\t   or (<V_TRACE> = 1 and lll.value = 'TRACE')\n" +
                "\t\t\t\t\t   or (<V_ERROR> = 1 and lll.value = 'ERROR')\n" +
                "\t\t\t\t\t   or (<V_FATAL> = 1 and lll.value = 'FATAL')\n" +
                "\t\t\t\t\t  )\n" +
                "\t\t\t\t\t)\n" +
                "\t\t\torder by decode(<V_SORT>, 0, lll.list_value_id, 1, lt.date_value, lt.date_value) asc\n" +
                "\t\t\t;";
        return jdbcTemplate.query(sql, new LogMapper());
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
                "and lt.attr_id = 23 /* Log timestamp */";
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
