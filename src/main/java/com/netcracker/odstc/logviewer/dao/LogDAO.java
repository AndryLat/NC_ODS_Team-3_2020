package com.netcracker.odstc.logviewer.dao;

import com.netcracker.odstc.logviewer.containers.RuleContainer;
import com.netcracker.odstc.logviewer.containers.SortType;
import com.netcracker.odstc.logviewer.containers.dto.LogDTO;
import com.netcracker.odstc.logviewer.mapper.LogDTOMapper;
import com.netcracker.odstc.logviewer.models.lists.LogLevel;
import org.springframework.data.domain.Page;
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
    private static final String GET_LOGS_BY_DIRECTORY_AND_RULE_AND_LEVEL_SORTED_QUERY = "SELECT /*+ index(logLevel Index_FOR_OBJECT_ID_ATTR_ID_LIST_VALUE_ID) index(logText Index_FOR_OBJECT_ID_ATTR_ID_VALUE) index(creationDate Index_FOR_OBJECT_ID_ATTR_ID_DATE_VALUE) index(OBJECTS Index_FOR_PARENT_ID) */ OBJECTS.OBJECT_ID, logText.VALUE, logLevel.LIST_VALUE_ID, creationDate.DATE_VALUE\n" +
            "FROM OBJECTS\n" +
            "         JOIN ATTRIBUTES logText\n" +
            "                   on OBJECTS.OBJECT_ID = logText.OBJECT_ID AND logText.attr_id = 23 AND upper(logText.value) like '%' || upper(:text) || '%'\n" +
            "         JOIN ATTRIBUTES logLevel\n" +
            "                   on OBJECTS.object_id = logLevel.OBJECT_ID AND logLevel.attr_id = 24 AND logLevel.list_value_id IN (:levels)\n" +
            "         JOIN ATTRIBUTES creationDate on OBJECTS.object_id = creationDate.OBJECT_ID AND creationDate.attr_id = 25 and\n" +
            "                                    creationDate.date_value BETWEEN nvl(:startDate,creationDate.date_value) and nvl(:endDate,creationDate.date_value)\n" +
            "WHERE OBJECTS.PARENT_ID IN (SELECT OBJECT_ID\n" +
            "                            FROM OBJECTS files\n" +
            "                            WHERE PARENT_ID = :directoryId)\n" +
            "order by logLevel.LIST_VALUE_ID,creationDate.date_value\n" +
            "OFFSET :offset ROWS FETCH NEXT :pageSize ROWS ONLY";

    private static final String GET_LOGS_BY_DIRECTORY_AND_RULE_AND_DATE_SORTED_QUERY = "SELECT /*+ index(logLevel Index_FOR_OBJECT_ID_ATTR_ID_LIST_VALUE_ID) index(logText Index_FOR_OBJECT_ID_ATTR_ID_VALUE) index(creationDate Index_FOR_OBJECT_ID_ATTR_ID_DATE_VALUE) index(OBJECTS Index_FOR_PARENT_ID) */ OBJECTS.OBJECT_ID, logText.VALUE, logLevel.LIST_VALUE_ID, creationDate.DATE_VALUE\n" +
            "FROM OBJECTS\n" +
            "         JOIN ATTRIBUTES logText\n" +
            "                   on OBJECTS.OBJECT_ID = logText.OBJECT_ID AND logText.attr_id = 23 AND upper(logText.value) like '%' || upper(:text) || '%'\n" +
            "         JOIN ATTRIBUTES logLevel\n" +
            "                   on OBJECTS.object_id = logLevel.OBJECT_ID AND logLevel.attr_id = 24 AND logLevel.list_value_id IN (:levels)\n" +
            "         JOIN ATTRIBUTES creationDate on OBJECTS.object_id = creationDate.OBJECT_ID AND creationDate.attr_id = 25 and\n" +
            "                                    creationDate.date_value BETWEEN nvl(:startDate,creationDate.date_value) and nvl(:endDate,creationDate.date_value)\n" +
            "WHERE OBJECTS.PARENT_ID IN (SELECT OBJECT_ID\n" +
            "                            FROM OBJECTS files\n" +
            "                            WHERE PARENT_ID = :directoryId)\n" +
            "order by creationDate.date_value\n" +
            "OFFSET :offset ROWS FETCH NEXT :pageSize ROWS ONLY";

    private static final String GET_LOGS_BY_FILE_AND_RULE_AND_LEVEL_SORTED_QUERY = "SELECT /*+ index(logLevel Index_FOR_OBJECT_ID_ATTR_ID_LIST_VALUE_ID) index(logText Index_FOR_OBJECT_ID_ATTR_ID_VALUE) index(creationDate Index_FOR_OBJECT_ID_ATTR_ID_DATE_VALUE) index(OBJECTS Index_FOR_PARENT_ID) */ OBJECTS.OBJECT_ID, logText.VALUE, logLevel.LIST_VALUE_ID, creationDate.DATE_VALUE\n" +
            "FROM OBJECTS\n" +
            "         JOIN ATTRIBUTES logText\n" +
            "                   on OBJECTS.OBJECT_ID = logText.OBJECT_ID AND logText.attr_id = 23 AND upper(logText.value) like '%' || upper(:text) || '%'\n" +
            "         JOIN ATTRIBUTES logLevel\n" +
            "                   on OBJECTS.object_id = logLevel.OBJECT_ID AND logLevel.attr_id = 24 AND logLevel.list_value_id IN (:levels)\n" +
            "         JOIN ATTRIBUTES creationDate on OBJECTS.object_id = creationDate.OBJECT_ID AND creationDate.attr_id = 25 and\n" +
            "                                    creationDate.date_value BETWEEN nvl(:startDate,creationDate.date_value) and nvl(:endDate,creationDate.date_value)\n" +
            "WHERE OBJECTS.PARENT_ID = :fileId\n" +
            "order by logLevel.LIST_VALUE_ID,creationDate.date_value\n" +
            "OFFSET :offset ROWS FETCH NEXT :pageSize ROWS ONLY";

    private static final String GET_LOGS_BY_FILE_AND_RULE_AND_DATE_SORTED_QUERY = "SELECT /*+ index(logLevel Index_FOR_OBJECT_ID_ATTR_ID_LIST_VALUE_ID) index(logText Index_FOR_OBJECT_ID_ATTR_ID_VALUE) index(creationDate Index_FOR_OBJECT_ID_ATTR_ID_DATE_VALUE) index(OBJECTS Index_FOR_PARENT_ID) */ OBJECTS.OBJECT_ID, logText.VALUE, logLevel.LIST_VALUE_ID, creationDate.DATE_VALUE\n" +
            "FROM OBJECTS\n" +
            "         JOIN ATTRIBUTES logText\n" +
            "                   on OBJECTS.OBJECT_ID = logText.OBJECT_ID AND logText.attr_id = 23 AND upper(logText.value) like '%' || upper(:text) || '%'\n" +
            "         JOIN ATTRIBUTES logLevel\n" +
            "                   on OBJECTS.object_id = logLevel.OBJECT_ID AND logLevel.attr_id = 24 AND logLevel.list_value_id IN (:levels)\n" +
            "         JOIN ATTRIBUTES creationDate on OBJECTS.object_id = creationDate.OBJECT_ID AND creationDate.attr_id = 25 and\n" +
            "                                    creationDate.date_value BETWEEN nvl(:startDate,creationDate.date_value) and nvl(:endDate,creationDate.date_value)\n" +
            "WHERE OBJECTS.PARENT_ID = :fileId\n" +
            "order by creationDate.date_value\n" +
            "OFFSET :offset ROWS FETCH NEXT :pageSize ROWS ONLY";

    private static final String GET_TOTAL_LOGS_COUNT_BY_DIRECTORY_AND_RULE_QUERY = "SELECT /*+ index(logLevel Index_FOR_OBJECT_ID_ATTR_ID_LIST_VALUE_ID) index(logText Index_FOR_OBJECT_ID_ATTR_ID_VALUE) index(creationDate Index_FOR_OBJECT_ID_ATTR_ID_DATE_VALUE) index(OBJECTS Index_FOR_PARENT_ID) */ COUNT(*)\n" +
            "FROM OBJECTS\n" +
            "         JOIN ATTRIBUTES logText\n" +
            "                   on OBJECTS.OBJECT_ID = logText.OBJECT_ID AND logText.attr_id = 23 AND upper(logText.value) like '%' || upper(:text) || '%'\n" +
            "         JOIN ATTRIBUTES logLevel\n" +
            "                   on OBJECTS.object_id = logLevel.OBJECT_ID AND logLevel.attr_id = 24 AND logLevel.list_value_id IN (:levels)\n" +
            "         JOIN ATTRIBUTES creationDate on OBJECTS.object_id = creationDate.OBJECT_ID AND creationDate.attr_id = 25 and\n" +
            "                                    creationDate.date_value BETWEEN nvl(:startDate,creationDate.date_value) and nvl(:endDate,creationDate.date_value)\n" +
            "WHERE OBJECTS.PARENT_ID IN (SELECT OBJECT_ID\n" +
            "                            FROM OBJECTS files\n" +
            "                            WHERE PARENT_ID = :directoryId)";

    private static final String GET_TOTAL_LOGS_COUNT_BY_FILE_AND_RULE_QUERY = "SELECT /*+ index(logLevel Index_FOR_OBJECT_ID_ATTR_ID_LIST_VALUE_ID) index(logText Index_FOR_OBJECT_ID_ATTR_ID_VALUE) index(creationDate Index_FOR_OBJECT_ID_ATTR_ID_DATE_VALUE) index(OBJECTS Index_FOR_PARENT_ID) */ COUNT(*)\n" +
            "FROM OBJECTS\n" +
            "         JOIN ATTRIBUTES logText\n" +
            "                   on OBJECTS.OBJECT_ID = logText.OBJECT_ID AND logText.attr_id = 23 AND upper(logText.value) like '%' || upper(:text) || '%'\n" +
            "         JOIN ATTRIBUTES logLevel\n" +
            "                   on OBJECTS.object_id = logLevel.OBJECT_ID AND logLevel.attr_id = 24 AND logLevel.list_value_id IN (:levels)\n" +
            "         JOIN ATTRIBUTES creationDate on OBJECTS.object_id = creationDate.OBJECT_ID AND creationDate.attr_id = 25 and\n" +
            "                                    creationDate.date_value BETWEEN nvl(:startDate,creationDate.date_value) and nvl(:endDate,creationDate.date_value)\n" +
            "WHERE OBJECTS.PARENT_ID = :fileId";

    private static final String GET_TOTAL_LOGS_APPROXIMATE_COUNT_BY_DIRECTORY_AND_RULE_QUERY = "SELECT /*+ index(logLevel Index_FOR_OBJECT_ID_ATTR_ID_LIST_VALUE_ID) index(logText Index_FOR_OBJECT_ID_ATTR_ID_VALUE) index(creationDate Index_FOR_OBJECT_ID_ATTR_ID_DATE_VALUE) index(OBJECTS Index_FOR_PARENT_ID) */ COUNT(*)*1000\n" +
            "FROM OBJECTS SAMPLE(0.1)\n" +
            "         JOIN ATTRIBUTES logText\n" +
            "                   on OBJECTS.OBJECT_ID = logText.OBJECT_ID AND logText.attr_id = 23 AND upper(logText.value) like '%' || upper(:text) || '%'\n" +
            "         JOIN ATTRIBUTES logLevel\n" +
            "                   on OBJECTS.object_id = logLevel.OBJECT_ID AND logLevel.attr_id = 24 AND logLevel.list_value_id IN (:levels)\n" +
            "         JOIN ATTRIBUTES creationDate on OBJECTS.object_id = creationDate.OBJECT_ID AND creationDate.attr_id = 25 and\n" +
            "                                    creationDate.date_value BETWEEN nvl(:startDate,creationDate.date_value) and nvl(:endDate,creationDate.date_value)\n" +
            "WHERE OBJECTS.PARENT_ID IN (SELECT OBJECT_ID\n" +
            "                            FROM OBJECTS files\n" +
            "                            WHERE PARENT_ID = :directoryId)";

    private static final String GET_TOTAL_LOGS_APPROXIMATE_COUNT_BY_FILE_AND_RULE_QUERY = "SELECT /*+ index(logLevel Index_FOR_OBJECT_ID_ATTR_ID_LIST_VALUE_ID) index(logText Index_FOR_OBJECT_ID_ATTR_ID_VALUE) index(creationDate Index_FOR_OBJECT_ID_ATTR_ID_DATE_VALUE) index(OBJECTS Index_FOR_PARENT_ID) */ COUNT(*)*1000\n" +
            "FROM OBJECTS SAMPLE(0.1)\n" +
            "         JOIN ATTRIBUTES logText\n" +
            "                   on OBJECTS.OBJECT_ID = logText.OBJECT_ID AND logText.attr_id = 23 AND upper(logText.value) like '%' || upper(:text) || '%'\n" +
            "         JOIN ATTRIBUTES logLevel\n" +
            "                   on OBJECTS.object_id = logLevel.OBJECT_ID AND logLevel.attr_id = 24 AND logLevel.list_value_id IN (:levels)\n" +
            "         JOIN ATTRIBUTES creationDate on OBJECTS.object_id = creationDate.OBJECT_ID AND creationDate.attr_id = 25 and\n" +
            "                                    creationDate.date_value BETWEEN nvl(:startDate,creationDate.date_value) and nvl(:endDate,creationDate.date_value)\n" +
            "WHERE OBJECTS.PARENT_ID = :fileId";

    public LogDAO(JdbcTemplate jdbcTemplate) {
        super(jdbcTemplate);
    }

    @Transactional
    public Page<LogDTO> getLogsByDirectoryId(BigInteger directoryId, RuleContainer ruleContainer, Pageable pageable) {

        MapSqlParameterSource parameterSourceCount = new MapSqlParameterSource()
                .addValue("directoryId", directoryId)
                .addValue("text", ruleContainer.getSearchText())
                .addValue("startDate", ruleContainer.getStartDate())
                .addValue("endDate", ruleContainer.getEndDate())
                .addValue("levels", convertEnumListToIntList(ruleContainer.getLevels()));

        NamedParameterJdbcTemplate namedParameterJdbcTemplate = new NamedParameterJdbcTemplate(jdbcTemplate);

        MapSqlParameterSource parameterSourceForObject = new MapSqlParameterSource(parameterSourceCount.getValues())
                .addValue("offset", pageable.getOffset())
                .addValue("pageSize", pageable.getPageSize());

        String query = ruleContainer.getSortType() == SortType.BY_DATE ? GET_LOGS_BY_DIRECTORY_AND_RULE_AND_DATE_SORTED_QUERY : GET_LOGS_BY_DIRECTORY_AND_RULE_AND_LEVEL_SORTED_QUERY;

        List<LogDTO> content = namedParameterJdbcTemplate.query(query, parameterSourceForObject, new LogDTOMapper());

        BigInteger approximateCount = namedParameterJdbcTemplate.queryForObject(GET_TOTAL_LOGS_APPROXIMATE_COUNT_BY_DIRECTORY_AND_RULE_QUERY, parameterSourceCount, BigInteger.class);
        BigInteger totalRows;
        boolean approximate;
        if (BigInteger.valueOf(100000).compareTo(approximateCount) > 0) {
            approximate = false;
            totalRows = namedParameterJdbcTemplate.queryForObject(GET_TOTAL_LOGS_COUNT_BY_DIRECTORY_AND_RULE_QUERY, parameterSourceCount, BigInteger.class);
        } else {
            approximate = true;
            totalRows = approximateCount;
        }

        if (totalRows == null) {
            totalRows = BigInteger.ZERO;
        }

        return new ApproximateTotalPage<>(content, pageable, totalRows.longValue(), approximate);
    }

    @Transactional
    public Page<LogDTO> getLogsByFileId(BigInteger fileId, RuleContainer ruleContainer, Pageable pageable) {
        NamedParameterJdbcTemplate namedParameterJdbcTemplate = new NamedParameterJdbcTemplate(jdbcTemplate);
        MapSqlParameterSource parameterSourceCount = new MapSqlParameterSource()
                .addValue("fileId", fileId)
                .addValue("text", ruleContainer.getSearchText())
                .addValue("startDate", ruleContainer.getStartDate())
                .addValue("endDate", ruleContainer.getEndDate())
                .addValue("levels", convertEnumListToIntList(ruleContainer.getLevels()));

        MapSqlParameterSource parameterSourceForObject = new MapSqlParameterSource(parameterSourceCount.getValues())
                .addValue("offset", pageable.getOffset())
                .addValue("pageSize", pageable.getPageSize());

        String query = ruleContainer.getSortType() == SortType.BY_DATE ? GET_LOGS_BY_FILE_AND_RULE_AND_DATE_SORTED_QUERY : GET_LOGS_BY_FILE_AND_RULE_AND_LEVEL_SORTED_QUERY;

        List<LogDTO> content = namedParameterJdbcTemplate.query(query, parameterSourceForObject, new LogDTOMapper());

        BigInteger approximateCount = namedParameterJdbcTemplate.queryForObject(GET_TOTAL_LOGS_APPROXIMATE_COUNT_BY_FILE_AND_RULE_QUERY, parameterSourceCount, BigInteger.class);
        BigInteger totalRows;
        boolean approximate;
        if (BigInteger.valueOf(100000).compareTo(approximateCount) > 0) {
            approximate = false;
            totalRows = namedParameterJdbcTemplate.queryForObject(GET_TOTAL_LOGS_COUNT_BY_FILE_AND_RULE_QUERY, parameterSourceCount, BigInteger.class);
        } else {
            approximate = true;
            totalRows = approximateCount;
        }

        if (totalRows == null) {
            totalRows = BigInteger.ZERO;
        }

        return new ApproximateTotalPage<>(content, pageable, totalRows.longValue(), approximate);
    }

    public BigInteger getLogsCountByDirectory(BigInteger directoryId, RuleContainer ruleContainer) {
        NamedParameterJdbcTemplate namedParameterJdbcTemplate = new NamedParameterJdbcTemplate(jdbcTemplate);

        MapSqlParameterSource parameterSourceCount = new MapSqlParameterSource()
                .addValue("directoryId", directoryId)
                .addValue("text", ruleContainer.getSearchText())
                .addValue("startDate", ruleContainer.getStartDate())
                .addValue("endDate", ruleContainer.getEndDate())
                .addValue("levels", convertEnumListToIntList(ruleContainer.getLevels()));

        return namedParameterJdbcTemplate.queryForObject(GET_TOTAL_LOGS_COUNT_BY_DIRECTORY_AND_RULE_QUERY, parameterSourceCount, BigInteger.class);
    }

    public BigInteger getLogsCountByFile(BigInteger fileId, RuleContainer ruleContainer) {
        NamedParameterJdbcTemplate namedParameterJdbcTemplate = new NamedParameterJdbcTemplate(jdbcTemplate);

        MapSqlParameterSource parameterSourceCount = new MapSqlParameterSource()
                .addValue("fileId", fileId)
                .addValue("text", ruleContainer.getSearchText())
                .addValue("startDate", ruleContainer.getStartDate())
                .addValue("endDate", ruleContainer.getEndDate())
                .addValue("levels", convertEnumListToIntList(ruleContainer.getLevels()));

        return namedParameterJdbcTemplate.queryForObject(GET_TOTAL_LOGS_APPROXIMATE_COUNT_BY_FILE_AND_RULE_QUERY, parameterSourceCount, BigInteger.class);
    }

    private List<Integer> convertEnumListToIntList(List<LogLevel> levels) {
        List<Integer> numericLevels = new ArrayList<>(levels.size());
        for (LogLevel level :
                levels) {
            numericLevels.add(level.getValue());
        }
        return numericLevels;
    }
}
