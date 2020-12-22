package com.netcracker.odstc.logviewer.dao;

import com.netcracker.odstc.logviewer.containers.AttributeObjectContainer;
import com.netcracker.odstc.logviewer.containers.HierarchyContainer;
import com.netcracker.odstc.logviewer.containers.converters.AOCConverter;
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
    private static final String ACTIVE_SERVER_DIRECTORY_FILES = "WITH active_servers AS (\n" +
            "    SELECT ATTR_ID,\n" +
            "           VALUE,\n" +
            "           DATE_VALUE,\n" +
            "           LIST_VALUE_ID,\n" +
            "           directory.OBJECT_ID,\n" +
            "           PARENT_ID,\n" +
            "           OBJECT_TYPE_ID,\n" +
            "           NAME\n" +
            "    FROM OBJECTS directory\n" +
            "             LEFT JOIN ATTRIBUTES A2 on directory.OBJECT_ID = A2.OBJECT_ID\n" +
            "    WHERE OBJECT_TYPE_ID = 2 --SERVER\n" +
            "      AND NOT EXISTS(SELECT OBJECT_ID\n" +
            "                     FROM ATTRIBUTES nonactive\n" +
            "                     WHERE (LIST_VALUE_ID = 6 OR LIST_VALUE_ID=8)\n" +
            "                       AND (nonactive.OBJECT_ID = directory.OBJECT_ID OR PARENT_ID = nonactive.OBJECT_ID)) -- IS ACTIVE\n" +
            "),\n" +
            "     active_directories AS (\n" +
            "         SELECT ATTR_ID,\n" +
            "                VALUE,\n" +
            "                DATE_VALUE,\n" +
            "                LIST_VALUE_ID,\n" +
            "                directory.OBJECT_ID,\n" +
            "                PARENT_ID,\n" +
            "                OBJECT_TYPE_ID,\n" +
            "                NAME\n" +
            "         FROM OBJECTS directory\n" +
            "                  LEFT JOIN ATTRIBUTES A2 on directory.OBJECT_ID = A2.OBJECT_ID\n" +
            "         WHERE OBJECT_TYPE_ID = 3                                  --DIRECTORY\n" +
            "           AND NOT EXISTS(SELECT OBJECT_ID\n" +
            "                          FROM ATTRIBUTES nonactive\n" +
            "                          WHERE (LIST_VALUE_ID = 10 OR LIST_VALUE_ID=12)\n" +
            "                            AND (nonactive.OBJECT_ID = directory.OBJECT_ID OR\n" +
            "                                 PARENT_ID = nonactive.OBJECT_ID)) -- IS ACTIVE\n" +
            "           AND PARENT_ID IN (SELECT OBJECT_ID FROM active_servers)) -- PARENT SERVER IS ACTIVE\n" +
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
            "SELECT ATTR_ID,\n" +
            "       VALUE,\n" +
            "       DATE_VALUE,\n" +
            "       LIST_VALUE_ID,\n" +
            "       logfile.OBJECT_ID,\n" +
            "       PARENT_ID,\n" +
            "       OBJECT_TYPE_ID,\n" +
            "       NAME\n" +
            "FROM OBJECTS logfile\n" +
            "         LEFT JOIN ATTRIBUTES A2 on logfile.OBJECT_ID = A2.OBJECT_ID\n" +
            "WHERE OBJECT_TYPE_ID = 4                                                                  --LOGFILE\n" +
            "  AND EXISTS(SELECT OBJECT_ID FROM active_directories WHERE OBJECT_ID = logfile.PARENT_ID)--PARENT IS ACTIVE";
    //TODO: Make sorted by USER_LAST_ACCESS. This cant be done on DB side, because we work with attributes, not objects

    private static final String NONACTIVE_SERVERS = "WITH nonactive_servers AS (\n" +
            "    SELECT ATTR_ID,\n" +
            "           VALUE,\n" +
            "           DATE_VALUE,\n" +
            "           LIST_VALUE_ID,\n" +
            "           server.OBJECT_ID,\n" +
            "           PARENT_ID,\n" +
            "           OBJECT_TYPE_ID,\n" +
            "           NAME\n" +
            "    FROM OBJECTS server\n" +
            "             LEFT JOIN ATTRIBUTES A2 on server.OBJECT_ID = A2.OBJECT_ID\n" +
            "    WHERE OBJECT_TYPE_ID = 2 --SERVER\n" +
            "      AND EXISTS(SELECT OBJECT_ID\n" +
            "                     FROM ATTRIBUTES active\n" +
            "                     WHERE (LIST_VALUE_ID = 8)\n" +
            "                       AND (active.OBJECT_ID = server.OBJECT_ID OR PARENT_ID = active.OBJECT_ID)MINUS\n" +
            "                 SELECT OBJECT_ID\n" +
            "                 FROM ATTRIBUTES\n" +
            "                 WHERE LIST_VALUE_ID = 6) -- NON ACTIVE BUT ENABLED\n" +
            "),\n" +
            "     directories AS (\n" +
            "         SELECT ATTR_ID,\n" +
            "                VALUE,\n" +
            "                DATE_VALUE,\n" +
            "                LIST_VALUE_ID,\n" +
            "                directory.OBJECT_ID,\n" +
            "                PARENT_ID,\n" +
            "                OBJECT_TYPE_ID,\n" +
            "                NAME\n" +
            "         FROM OBJECTS directory\n" +
            "                  LEFT JOIN ATTRIBUTES A2 on directory.OBJECT_ID = A2.OBJECT_ID\n" +
            "         WHERE OBJECT_TYPE_ID = 3 --DIRECTORY\n" +
            "           AND PARENT_ID IN (SELECT OBJECT_ID FROM nonactive_servers)) -- PARENT SERVER IS NONACTIVE\n" +
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

    private static final String ACTIVE_SERVERS_NONACTIVE_DIRECTORY = "WITH active_servers AS (\n" +
            "    SELECT ATTR_ID,\n" +
            "           VALUE,\n" +
            "           DATE_VALUE,\n" +
            "           LIST_VALUE_ID,\n" +
            "           server.OBJECT_ID,\n" +
            "           PARENT_ID,\n" +
            "           OBJECT_TYPE_ID,\n" +
            "           NAME\n" +
            "    FROM OBJECTS server\n" +
            "             LEFT JOIN ATTRIBUTES A2 on server.OBJECT_ID = A2.OBJECT_ID\n" +
            "    WHERE OBJECT_TYPE_ID = 2 --SERVER\n" +
            "      AND EXISTS(SELECT OBJECT_ID\n" +
            "                 FROM ATTRIBUTES active\n" +
            "                 WHERE LIST_VALUE_ID = 7\n" +
            "                   AND (active.OBJECT_ID = server.OBJECT_ID OR PARENT_ID = active.OBJECT_ID)\n" +
            "                 MINUS\n" +
            "                 SELECT OBJECT_ID\n" +
            "                 FROM ATTRIBUTES\n" +
            "                 WHERE LIST_VALUE_ID = 6) -- IS ACTIVE AND ENABLED\n" +
            "),\n" +
            "     nonactive_directories AS (\n" +
            "         SELECT ATTR_ID,\n" +
            "                VALUE,\n" +
            "                DATE_VALUE,\n" +
            "                LIST_VALUE_ID,\n" +
            "                directory.OBJECT_ID,\n" +
            "                PARENT_ID,\n" +
            "                OBJECT_TYPE_ID,\n" +
            "                NAME\n" +
            "         FROM OBJECTS directory\n" +
            "                  LEFT JOIN ATTRIBUTES A2 on directory.OBJECT_ID = A2.OBJECT_ID\n" +
            "         WHERE OBJECT_TYPE_ID = 3                   --DIRECTORY\n" +
            "           AND NOT EXISTS(SELECT OBJECT_ID\n" +
            "                          FROM ATTRIBUTES active\n" +
            "                          WHERE LIST_VALUE_ID = 11\n" +
            "                            AND (active.OBJECT_ID = directory.OBJECT_ID OR\n" +
            "                                 PARENT_ID = active.OBJECT_ID)\n" +
            "                          MINUS\n" +
            "                          SELECT OBJECT_ID\n" +
            "                          FROM ATTRIBUTES\n" +
            "                          WHERE LIST_VALUE_ID = 10) -- IS ACTIVE\n" +
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
    private final AOCConverter aocConverter;

    public ContainerDAO(JdbcTemplate jdbcTemplate) {
        super(jdbcTemplate);
        aocConverter = new AOCConverter();
    }

    public List<HierarchyContainer> getActiveServersWithChildren() {
        Map<BigInteger, HierarchyContainer> hierarchyContainerMap = getHierarchyContainerFromQuery(ACTIVE_SERVER_DIRECTORY_FILES);

        validateServerContainer(hierarchyContainerMap);
        validateChildrenNotEmpty(hierarchyContainerMap);

        return new ArrayList<>(hierarchyContainerMap.values());
    }

    public List<HierarchyContainer> getNonactiveServers() {
        Map<BigInteger, HierarchyContainer> hierarchyContainerMap = getHierarchyContainerFromQuery(NONACTIVE_SERVERS);

        validateServerContainer(hierarchyContainerMap);

        return new ArrayList<>(hierarchyContainerMap.values());
    }

    public List<HierarchyContainer> getActiveServersWithNonactiveDirectories() {
        Map<BigInteger, HierarchyContainer> hierarchyContainerMap = getHierarchyContainerFromQuery(ACTIVE_SERVERS_NONACTIVE_DIRECTORY);

        validateServerContainer(hierarchyContainerMap);
        validateChildrenNotEmpty(hierarchyContainerMap);

        return new ArrayList<>(hierarchyContainerMap.values());
    }

    private Map<BigInteger, HierarchyContainer> getHierarchyContainerFromQuery(String query) {
        List<AttributeObjectContainer> eavObjectsContainers = jdbcTemplate.query(query, new AttributeObjectMapper());

        return aocConverter.convertAOCtoHC(eavObjectsContainers);
    }

    private void validateServerContainer(Map<BigInteger, HierarchyContainer> hierarchyContainerMap) {
        hierarchyContainerMap.entrySet().removeIf(serverContainer ->
                !Server.class.isAssignableFrom(serverContainer.getValue().getOriginal().getClass()));
    }

    private void validateChildrenNotEmpty(Map<BigInteger, HierarchyContainer> hierarchyContainerMap) {
        hierarchyContainerMap.entrySet().removeIf(serverContainer -> serverContainer.getValue().getChildren().isEmpty());
    }
}
