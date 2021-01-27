package com.netcracker.odstc.logviewer.dao;

import com.netcracker.odstc.logviewer.containers.dto.LogDTO;
import com.netcracker.odstc.logviewer.mapper.LogDTOMapper;
import com.netcracker.odstc.logviewer.models.lists.LogLevel;
import com.netcracker.odstc.logviewer.service.RuleContainer;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

@Repository
public class LogDAO extends EAVObjectDAO {
    @SuppressWarnings({"squid:S1192"})//Suppress duplications in sql
    private static final String GET_ALL_BY_RULE_AND_LEVEL_SORTED_QUERY = "SELECT /*+ index(A3 for2) index(a2 forobval) */ OBJECTS.OBJECT_ID, logText.VALUE, logLevel.LIST_VALUE_ID, creationDate.DATE_VALUE\n" +
            "FROM OBJECTS\n" +
            "         JOIN ATTRIBUTES logText\n" +
            "                   on OBJECTS.OBJECT_ID = logText.OBJECT_ID AND logText.attr_id = 23 AND logText.value like '%' || :text || '%'\n" +
            "         JOIN ATTRIBUTES logLevel\n" +
            "                   on OBJECTS.object_id = logLevel.OBJECT_ID AND logLevel.attr_id = 24 AND logLevel.list_value_id IN (:levels)\n" +
            "         JOIN ATTRIBUTES creationDate on OBJECTS.object_id = creationDate.OBJECT_ID AND creationDate.attr_id = 25 and\n" +
            "                                    creationDate.date_value BETWEEN nvl(:startDate,creationDate.date_value) and nvl(:endDate,creationDate.date_value)\n" +
            "WHERE OBJECTS.PARENT_ID IN (SELECT OBJECT_ID\n" +
            "                            FROM OBJECTS files\n" +
            "                            WHERE PARENT_ID = :directoryId)\n" +
            "order by logLevel.LIST_VALUE_ID,creationDate.date_value\n" +
            "OFFSET :offset ROWS FETCH NEXT :pageSize ROWS ONLY";
    private static final String GET_ALL_BY_RULE_AND_DATE_SORTED_QUERY = "SELECT /*+ index(A3 for2) index(a2 forobval) */ OBJECTS.OBJECT_ID, logText.VALUE, logLevel.LIST_VALUE_ID, creationDate.DATE_VALUE\n" +
            "FROM OBJECTS\n" +
            "         JOIN ATTRIBUTES logText\n" +
            "                   on OBJECTS.OBJECT_ID = logText.OBJECT_ID AND logText.attr_id = 23 AND logText.value like '%' || :text || '%'\n" +
            "         JOIN ATTRIBUTES logLevel\n" +
            "                   on OBJECTS.object_id = logLevel.OBJECT_ID AND logLevel.attr_id = 24 AND logLevel.list_value_id IN (:levels)\n" +
            "         JOIN ATTRIBUTES creationDate on OBJECTS.object_id = creationDate.OBJECT_ID AND creationDate.attr_id = 25 and\n" +
            "                                    creationDate.date_value BETWEEN nvl(:startDate,creationDate.date_value) and nvl(:endDate,creationDate.date_value)\n" +
            "WHERE OBJECTS.PARENT_ID IN (SELECT OBJECT_ID\n" +
            "                            FROM OBJECTS files\n" +
            "                            WHERE PARENT_ID = :directoryId)\n" +
            "order by creationDate.date_value\n" +
            "OFFSET :offset ROWS FETCH NEXT :pageSize ROWS ONLY";
    private static final String GET_TOTAL_COUNT_LOGS_QUERY = "SELECT /*+ index(A3 for2) index(a2 forobval) */ COUNT(*)\n" +
            "FROM OBJECTS\n" +
            "         JOIN ATTRIBUTES logText\n" +
            "                   on OBJECTS.OBJECT_ID = logText.OBJECT_ID AND logText.attr_id = 23 AND logText.value like '%' || :text || '%'\n" +
            "         JOIN ATTRIBUTES logLevel\n" +
            "                   on OBJECTS.object_id = logLevel.OBJECT_ID AND logLevel.attr_id = 24 AND logLevel.list_value_id IN (:levels)\n" +
            "         JOIN ATTRIBUTES creationDate on OBJECTS.object_id = creationDate.OBJECT_ID AND creationDate.attr_id = 25 and\n" +
            "                                    creationDate.date_value BETWEEN nvl(:startDate,creationDate.date_value) and nvl(:endDate,creationDate.date_value)\n" +
            "WHERE OBJECTS.PARENT_ID IN (SELECT OBJECT_ID\n" +
            "                            FROM OBJECTS files\n" +
            "                            WHERE PARENT_ID = :directoryId)";

    public LogDAO(JdbcTemplate jdbcTemplate) {
        super(jdbcTemplate);
    }

    @Transactional
    public Page<LogDTO> getLogByAll(BigInteger directoryId, RuleContainer ruleContainer, Pageable pageable) {
        NamedParameterJdbcTemplate namedParameterJdbcTemplate = new NamedParameterJdbcTemplate(jdbcTemplate);

        MapSqlParameterSource parameterSourceCount = new MapSqlParameterSource()
                .addValue("directoryId", directoryId)
                .addValue("text", ruleContainer.getText())
                .addValue("startDate", ruleContainer.getDat1())
                .addValue("endDate", ruleContainer.getDat2())
                .addValue("levels", convertEnumListToIntList(ruleContainer.getLevels()));

        MapSqlParameterSource parameterSourceForObject = new MapSqlParameterSource(parameterSourceCount.getValues())
                .addValue("offset", pageable.getOffset())
                .addValue("pageSize", pageable.getPageSize());

        String query = ruleContainer.getSort() == 0 ? GET_ALL_BY_RULE_AND_DATE_SORTED_QUERY : GET_ALL_BY_RULE_AND_LEVEL_SORTED_QUERY;

        List<LogDTO> content = namedParameterJdbcTemplate.query(query, parameterSourceForObject, new LogDTOMapper());

        BigInteger totalRows = namedParameterJdbcTemplate.queryForObject(GET_TOTAL_COUNT_LOGS_QUERY, parameterSourceCount, BigInteger.class);

        if (totalRows == null) {
            totalRows = BigInteger.ZERO;
        }

        return new PageImpl<>(content, pageable, totalRows.longValue());
    }
    private List<Integer> convertEnumListToIntList(List<LogLevel> levels){
        List<Integer> levelsInInt = new ArrayList<>(levels.size());
        for (LogLevel level :
                levels) {
            levelsInInt.add(level.getValue());
        }
        return levelsInInt;
    }
}
