package com.netcracker.odstc.logviewer.dao;

import com.netcracker.odstc.logviewer.containers.dto.LogDTO;
import com.netcracker.odstc.logviewer.mapper.LogDTOMapper;
import com.netcracker.odstc.logviewer.service.RuleContainer;
import org.springframework.data.domain.Pageable;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.stereotype.Repository;

import java.math.BigInteger;
import java.util.List;

@Repository
public class LogDAO extends EAVObjectDAO {

    public LogDAO(JdbcTemplate jdbcTemplate) {
        super(jdbcTemplate);
    }

    private static final String GET_ALL_BY_RULE_AND_LEVEL_SORTED_QUERY = "WITH files AS (\n" +
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

    private static final String GET_ALL_BY_RULE_AND_DATE_SORTED_QUERY = "WITH files AS (\n" +
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
            "  and creationDate.date_value BETWEEN  nvl(TO_TIMESTAMP ('10-12-02 14:10:10.123000', 'DD-MM-RR HH24:MI:SS.FF'), creationDate.date_value) and nvl(TO_TIMESTAMP ('10-01-22 14:10:10.123000', 'DD-MM-RR HH24:MI:SS.FF'), creationDate.date_value)\n" +
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
            "order by creationDate.DATE_VALUE\n" +
            "OFFSET :offset ROWS FETCH NEXT :pageSize ROWS ONLY";

    public List<LogDTO> getLogByAll(BigInteger directoryId, RuleContainer ruleContainer, Pageable pageable) {


        SqlParameterSource parameterSource = new MapSqlParameterSource()
                .addValue("directoryId",directoryId)
                .addValue("text",        ruleContainer.getText())
                .addValue("startDate",ruleContainer.getDat1())
                .addValue("endDate",ruleContainer.getDat2())
                .addValue("V_SEVERE",ruleContainer.getSevere())
                .addValue("V_WARNING",ruleContainer.getWarning())
                .addValue("V_INFO",ruleContainer.getInfo())
                .addValue("V_CONFIG",ruleContainer.getConfig())
                .addValue("V_FINE",ruleContainer.getFine())
                .addValue("V_FINER",ruleContainer.getFiner())
                .addValue("V_FINEST",ruleContainer.getFinest())
                .addValue("V_DEBUG",ruleContainer.getDebug())
                .addValue("V_TRACE",ruleContainer.getTrace())
                .addValue("V_ERROR",ruleContainer.getError())
                .addValue("V_FATAL",ruleContainer.getFatal())
                .addValue("offset",pageable.getOffset())
                .addValue("pageSize",pageable.getPageSize());

        String query = ruleContainer.getSort()==0? GET_ALL_BY_RULE_AND_DATE_SORTED_QUERY:GET_ALL_BY_RULE_AND_LEVEL_SORTED_QUERY;

        return new NamedParameterJdbcTemplate(jdbcTemplate).query(query,parameterSource, new LogDTOMapper());
    }
}
