package com.netcracker.odstc.logviewer.dao;

import com.netcracker.odstc.logviewer.models.Log;
import com.netcracker.odstc.logviewer.models.lists.LogLevel;
import org.springframework.data.domain.Pageable;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.stereotype.Repository;

import java.math.BigInteger;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Repository
public class LogDAO extends EAVObjectDAO {

    public LogDAO(JdbcTemplate jdbcTemplate) {
        super(jdbcTemplate);
    }

    private static final String GET_ALL_BY_RULE_QUERY = "WITH files AS (\n" +
            "    SELECT OBJECT_ID\n" +
            "    FROM OBJECTS files\n" +
            "    WHERE PARENT_ID = :directoryId\n" +
            ")\n" +
            "select ob.object_id     as id,\n" +
            "       text.value        as fcl_value,\n" +
            "       logLevel.LIST_VALUE_ID as log_level_value,\n" +
            "       creationDate.date_value as log_timestamp_value\n" +
            "from objects ob\n" +
            "         left join attributes text on text.object_id = ob.object_id\n" +
            "         left join attributes logLevel on logLevel.object_id = ob.object_id\n" +
            "         left join attributes creationDate on creationDate.object_id = ob.object_id\n" +
            "where ob.object_type_id = 5\n" +
            "  and text.attr_id = 23 /* Full content of log */\n" +
            "  and logLevel.attr_id = 24 /* Log level */\n" +
            "  and creationDate.attr_id = 25 /* Log timestamp */\n" +
            "  and ob.PARENT_ID IN (SELECT OBJECT_ID FROM files)\n" +
            "  and text.value like '%' || :text || '%'\n" +
            "  and creationDate.date_value BETWEEN  nvl(:startDate, creationDate.date_value) and nvl(:endDate, creationDate.date_value)\n" +
            "  and (\n" +
            "        (\n" +
            "                    :V_SEVERE + :V_WARNING + :V_INFO + :V_CONFIG + :V_FINE + :V_FINER\n" +
            "                    + :V_FINEST + :V_DEBUG + :V_TRACE + :V_ERROR + :V_FATAL = 0\n" +
            "            )\n" +
            "        or\n" +
            "        (\n" +
            "                (:V_SEVERE = 1 and logLevel.LIST_VALUE_ID = 13)\n" +
            "                or (:V_WARNING = 1 and logLevel.LIST_VALUE_ID = 14)\n" +
            "                or (:V_INFO = 1 and logLevel.LIST_VALUE_ID = 15)\n" +
            "                or (:V_CONFIG = 1 and logLevel.LIST_VALUE_ID = 16)\n" +
            "                or (:V_FINE = 1 and logLevel.LIST_VALUE_ID = 17)\n" +
            "                or (:V_FINER = 1 and logLevel.LIST_VALUE_ID = 18)\n" +
            "                or (:V_FINEST = 1 and logLevel.LIST_VALUE_ID = 19)\n" +
            "                or (:V_DEBUG = 1 and logLevel.LIST_VALUE_ID = 20)\n" +
            "                or (:V_TRACE = 1 and logLevel.LIST_VALUE_ID = 21)\n" +
            "                or (:V_ERROR = 1 and logLevel.LIST_VALUE_ID = 22)\n" +
            "                or (:V_FATAL = 1 and logLevel.LIST_VALUE_ID = 23)\n" +
            "            )\n" +
            "    )\n" +
            "order by logLevel.list_value_id, creationDate.date_value asc\n" +
            "OFFSET :offset ROWS FETCH NEXT :pageSize ROWS ONLY\n";

    public List<Log> getLogByAll(BigInteger directoryId, String text, Date dat1, Date dat2, int V_SEVERE, int V_WARNING,
                                 int V_INFO, int V_CONFIG, int V_FINE, int V_FINER, int V_FINEST, int V_DEBUG,
                                 int V_TRACE, int V_ERROR, int V_FATAL, int V_SORT, Pageable pageable) {

        SqlParameterSource parameterSource = new MapSqlParameterSource()
                .addValue("directoryId",directoryId)
                .addValue("text",text)
                .addValue("startDate",dat1)
                .addValue("endDate",dat2)
                .addValue("V_SEVERE",V_SEVERE)
                .addValue("V_WARNING",V_WARNING)
                .addValue("V_INFO",V_INFO)
                .addValue("V_CONFIG",V_CONFIG)
                .addValue("V_FINE",V_FINE)
                .addValue("V_FINER",V_FINER)
                .addValue("V_FINEST",V_FINEST)
                .addValue("V_DEBUG",V_DEBUG)
                .addValue("V_TRACE",V_TRACE)
                .addValue("V_ERROR",V_ERROR)
                .addValue("V_FATAL",V_FATAL)
                .addValue("V_SORT",V_SORT)
                .addValue("offset",pageable.getOffset())
                .addValue("pageSize",pageable.getPageSize());

        List<BigInteger> objectIds = new NamedParameterJdbcTemplate(jdbcTemplate).query(GET_ALL_BY_RULE_QUERY,parameterSource, new ObjectIdMapper());

        List<Log> logs = new ArrayList<>();

        for (BigInteger id : objectIds) {
            logs.add(getObjectById(id,Log.class));
        }

        return logs;
    }


    private static class ObjectIdMapper implements RowMapper<BigInteger> {

        @Override
        public BigInteger mapRow(ResultSet resultSet, int i) throws SQLException {
            return resultSet.getBigDecimal("id").toBigInteger();
        }
    }

    private static class LogMapper implements RowMapper<Log> {

        @Override
        public Log mapRow(ResultSet resultSet, int i) throws SQLException {
            Log log = new Log();
            log.setObjectId(resultSet.getBigDecimal("id").toBigInteger());
            log.setCreationDate(resultSet.getTimestamp("log_timestamp_value"));
            log.setLevel(LogLevel.getByID(resultSet.getBigDecimal("log_level_value").toBigInteger().intValue()));
            log.setText(resultSet.getString("fcl_value"));
            return log;
        }
    }
}
