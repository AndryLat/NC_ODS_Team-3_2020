package com.netcracker.odstc.logviewer.dao;

import com.netcracker.odstc.logviewer.containers.AttributeObjectContainer;
import com.netcracker.odstc.logviewer.containers.HierarchyContainer;
import com.netcracker.odstc.logviewer.containers.converters.AttributeObjectContainerConverter;
import com.netcracker.odstc.logviewer.mapper.AttributeObjectMapper;
import com.netcracker.odstc.logviewer.models.Server;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Repository
public class ContainerDAO extends EAVObjectDAO {
    @SuppressWarnings({"squid:S1192"})//Suppress duplications in sql
    private static final String GET_ACTIVE_SERVERS_WITH_DIRECTORIES_WITH_FILES_QUERY = "WITH active_servers AS (\n" +
            "    SELECT /*+ index(A2 INDEX_FOR_OBJECT_ID) */ ATTR_ID,\n" +
            "           VALUE,\n" +
            "           DATE_VALUE,\n" +
            "           LIST_VALUE_ID,\n" +
            "           directory.OBJECT_ID,\n" +
            "           PARENT_ID,\n" +
            "           OBJECT_TYPE_ID,\n" +
            "           NAME\n" +
            "    FROM OBJECTS directory\n" +
            "             LEFT JOIN ATTRIBUTES A2 on directory.OBJECT_ID = A2.OBJECT_ID\n" +
            "    WHERE OBJECT_TYPE_ID = 2 /* SERVER */\n" +
            "      AND NOT EXISTS(SELECT OBJECT_ID\n" +
            "                     FROM ATTRIBUTES nonactive\n" +
            "                     WHERE (LIST_VALUE_ID = 6 OR LIST_VALUE_ID=8) /* FALSE */\n" +
            "                       AND (nonactive.OBJECT_ID = directory.OBJECT_ID OR PARENT_ID = nonactive.OBJECT_ID))\n" +
            "),\n" +
            "     active_directories AS (\n" +
            "         SELECT /*+ index(A2 INDEX_FOR_OBJECT_ID) */ ATTR_ID,\n" +
            "                VALUE,\n" +
            "                DATE_VALUE,\n" +
            "                LIST_VALUE_ID,\n" +
            "                directory.OBJECT_ID,\n" +
            "                PARENT_ID,\n" +
            "                OBJECT_TYPE_ID,\n" +
            "                NAME\n" +
            "         FROM OBJECTS directory\n" +
            "                  LEFT JOIN ATTRIBUTES A2 on directory.OBJECT_ID = A2.OBJECT_ID\n" +
            "         WHERE OBJECT_TYPE_ID = 3                                  /* DIRECTORY */\n" +
            "           AND NOT EXISTS(SELECT OBJECT_ID\n" +
            "                          FROM ATTRIBUTES nonactive\n" +
            "                          WHERE (LIST_VALUE_ID = 10 OR LIST_VALUE_ID=12) /* FALSE */\n" +
            "                            AND (nonactive.OBJECT_ID = directory.OBJECT_ID OR\n" +
            "                                 PARENT_ID = nonactive.OBJECT_ID)) \n" +
            "           AND PARENT_ID IN (SELECT OBJECT_ID FROM active_servers)) \n" +
            "SELECT *\n" +
            "FROM active_servers\n" +
            "UNION ALL\n" +
            "SELECT ATTR_ID,\n" +
            "       VALUE,\n" +
            "       DATE_VALUE,\n" +
            "       LIST_VALUE_ID,\n" +
            "       OBJECT_ID,\n" +
            "       PARENT_ID,\n" +
            "       OBJECT_TYPE_ID,\n" +
            "       NAME\n" +
            "FROM active_directories\n" +
            "UNION ALL\n" +
            "SELECT /*+ index(A2 INDEX_FOR_OBJECT_ID) */ ATTR_ID,\n" +
            "       VALUE,\n" +
            "       DATE_VALUE,\n" +
            "       LIST_VALUE_ID,\n" +
            "       logfile.OBJECT_ID,\n" +
            "       PARENT_ID,\n" +
            "       OBJECT_TYPE_ID,\n" +
            "       NAME\n" +
            "FROM OBJECTS logfile\n" +
            "         LEFT JOIN ATTRIBUTES A2 on logfile.OBJECT_ID = A2.OBJECT_ID\n" +
            "WHERE OBJECT_TYPE_ID = 4   /* LOGFILE */                                                             \n" +
            "  AND EXISTS(SELECT OBJECT_ID FROM active_directories WHERE OBJECT_ID = logfile.PARENT_ID)";

    private static final String GET_NONACTIVE_SERVERS_WITH_DIRECTORIES_QUERY = "WITH nonactive_servers AS (\n" +
            "    SELECT /*+ index(A2 INDEX_FOR_OBJECT_ID) */ ATTR_ID,\n" +
            "           VALUE,\n" +
            "           DATE_VALUE,\n" +
            "           LIST_VALUE_ID,\n" +
            "           server.OBJECT_ID,\n" +
            "           PARENT_ID,\n" +
            "           OBJECT_TYPE_ID,\n" +
            "           NAME\n" +
            "    FROM OBJECTS server\n" +
            "             LEFT JOIN ATTRIBUTES A2 on server.OBJECT_ID = A2.OBJECT_ID\n" +
            "    WHERE OBJECT_TYPE_ID = 2 /* SERVER */\n" +
            "      AND EXISTS(SELECT OBJECT_ID\n" +
            "                     FROM ATTRIBUTES active\n" +
            "                     WHERE (LIST_VALUE_ID = 8)\n" +
            "                       AND (active.OBJECT_ID = server.OBJECT_ID OR PARENT_ID = active.OBJECT_ID)MINUS\n" +
            "                 SELECT OBJECT_ID\n" +
            "                 FROM ATTRIBUTES\n" +
            "                 WHERE LIST_VALUE_ID = 6) /* FALSE */\n" +
            "),\n" +
            "     directories AS (\n" +
            "         SELECT /*+ index(A2 INDEX_FOR_OBJECT_ID) */ ATTR_ID,\n" +
            "                VALUE,\n" +
            "                DATE_VALUE,\n" +
            "                LIST_VALUE_ID,\n" +
            "                directory.OBJECT_ID,\n" +
            "                PARENT_ID,\n" +
            "                OBJECT_TYPE_ID,\n" +
            "                NAME\n" +
            "         FROM OBJECTS directory\n" +
            "                  LEFT JOIN ATTRIBUTES A2 on directory.OBJECT_ID = A2.OBJECT_ID\n" +
            "         WHERE OBJECT_TYPE_ID = 3 /* DIRECTORY */\n" +
            "           AND PARENT_ID IN (SELECT OBJECT_ID FROM nonactive_servers)) \n" +
            "SELECT ATTR_ID,\n" +
            "       VALUE,\n" +
            "       DATE_VALUE,\n" +
            "       LIST_VALUE_ID,\n" +
            "       OBJECT_ID,\n" +
            "       PARENT_ID,\n" +
            "       OBJECT_TYPE_ID,\n" +
            "       NAME\n" +
            "FROM directories\n" +
            "UNION ALL\n" +
            "SELECT *\n" +
            "FROM nonactive_servers";

    private static final String GET_ACTIVE_SERVERS_NONACTIVE_DIRECTORIES_QUERY = "WITH active_servers AS (\n" +
            "    SELECT /*+ index(A2 INDEX_FOR_OBJECT_ID) */ ATTR_ID,\n" +
            "           VALUE,\n" +
            "           DATE_VALUE,\n" +
            "           LIST_VALUE_ID,\n" +
            "           server.OBJECT_ID,\n" +
            "           PARENT_ID,\n" +
            "           OBJECT_TYPE_ID,\n" +
            "           NAME\n" +
            "    FROM OBJECTS server\n" +
            "             LEFT JOIN ATTRIBUTES A2 on server.OBJECT_ID = A2.OBJECT_ID\n" +
            "    WHERE OBJECT_TYPE_ID = 2 /* SERVER */\n" +
            "      AND EXISTS(SELECT OBJECT_ID\n" +
            "                 FROM ATTRIBUTES active\n" +
            "                 WHERE LIST_VALUE_ID = 7 /* TRUE */\n" +
            "                   AND (active.OBJECT_ID = server.OBJECT_ID OR PARENT_ID = active.OBJECT_ID)\n" +
            "                 MINUS\n" +
            "                 SELECT OBJECT_ID\n" +
            "                 FROM ATTRIBUTES\n" +
            "                 WHERE LIST_VALUE_ID = 6) --/* FALSE */\n" +
            "),\n" +
            "     nonactive_directories AS (\n" +
            "         SELECT /*+ index(A2 INDEX_FOR_OBJECT_ID) */ ATTR_ID,\n" +
            "                VALUE,\n" +
            "                DATE_VALUE,\n" +
            "                LIST_VALUE_ID,\n" +
            "                directory.OBJECT_ID,\n" +
            "                PARENT_ID,\n" +
            "                OBJECT_TYPE_ID,\n" +
            "                NAME\n" +
            "         FROM OBJECTS directory\n" +
            "                  LEFT JOIN ATTRIBUTES A2 on directory.OBJECT_ID = A2.OBJECT_ID\n" +
            "         WHERE OBJECT_TYPE_ID = 3                   /* DIRECTORY */\n" +
            "           AND NOT EXISTS(SELECT OBJECT_ID\n" +
            "                          FROM ATTRIBUTES active\n" +
            "                          WHERE LIST_VALUE_ID = 11 /* TRUE */\n" +
            "                            AND (active.OBJECT_ID = directory.OBJECT_ID OR\n" +
            "                                 PARENT_ID = active.OBJECT_ID)\n" +
            "                          MINUS\n" +
            "                          SELECT OBJECT_ID\n" +
            "                          FROM ATTRIBUTES\n" +
            "                          WHERE LIST_VALUE_ID = 10) /* FALSE */\n" +
            "           AND PARENT_ID IN (SELECT OBJECT_ID FROM active_servers)) -- PARENT SERVER IS NONACTIVE\n" +
            "SELECT ATTR_ID,\n" +
            "       VALUE,\n" +
            "       DATE_VALUE,\n" +
            "       LIST_VALUE_ID,\n" +
            "       OBJECT_ID,\n" +
            "       PARENT_ID,\n" +
            "       OBJECT_TYPE_ID,\n" +
            "       NAME\n" +
            "FROM nonactive_directories\n" +
            "UNION ALL\n" +
            "SELECT *\n" +
            "FROM active_servers";

    private final AttributeObjectContainerConverter attributeObjectContainerConverter;

    public ContainerDAO(JdbcTemplate jdbcTemplate) {
        super(jdbcTemplate);
        attributeObjectContainerConverter = new AttributeObjectContainerConverter();
    }

    public List<HierarchyContainer> getActiveServersWithChildren() {
        Map<BigInteger, HierarchyContainer> hierarchyContainerMap = getHierarchyContainerFromQuery(GET_ACTIVE_SERVERS_WITH_DIRECTORIES_WITH_FILES_QUERY);

        excludeNotServerContainers(hierarchyContainerMap);
        excludeEmptyContainers(hierarchyContainerMap);

        return new ArrayList<>(hierarchyContainerMap.values());
    }

    public List<HierarchyContainer> getNonactiveServers() {
        Map<BigInteger, HierarchyContainer> hierarchyContainerMap = getHierarchyContainerFromQuery(GET_NONACTIVE_SERVERS_WITH_DIRECTORIES_QUERY);

        excludeNotServerContainers(hierarchyContainerMap);

        return new ArrayList<>(hierarchyContainerMap.values());
    }

    public List<HierarchyContainer> getActiveServersWithNonactiveDirectories() {
        Map<BigInteger, HierarchyContainer> hierarchyContainerMap = getHierarchyContainerFromQuery(GET_ACTIVE_SERVERS_NONACTIVE_DIRECTORIES_QUERY);

        excludeNotServerContainers(hierarchyContainerMap);
        excludeEmptyContainers(hierarchyContainerMap);

        return new ArrayList<>(hierarchyContainerMap.values());
    }

    private Map<BigInteger, HierarchyContainer> getHierarchyContainerFromQuery(String query) {
        List<AttributeObjectContainer> eavObjectsContainers = jdbcTemplate.query(query, new AttributeObjectMapper());

        return attributeObjectContainerConverter.convertAttributeObjectContainerToHierarchyContainer(eavObjectsContainers);
    }

    private void excludeNotServerContainers(Map<BigInteger, HierarchyContainer> hierarchyContainerMap) {
        hierarchyContainerMap.entrySet().removeIf(serverContainer ->
                !Server.class.isAssignableFrom(serverContainer.getValue().getOriginal().getClass()));
    }

    private void excludeEmptyContainers(Map<BigInteger, HierarchyContainer> hierarchyContainerMap) {
        hierarchyContainerMap.entrySet().removeIf(serverContainer ->
                serverContainer.getValue().getChildren().isEmpty());
    }
}
