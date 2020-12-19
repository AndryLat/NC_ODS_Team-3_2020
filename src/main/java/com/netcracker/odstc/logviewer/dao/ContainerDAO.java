package com.netcracker.odstc.logviewer.dao;

import com.netcracker.odstc.logviewer.containers.HierarchyContainer;
import com.netcracker.odstc.logviewer.containers.converters.AOCConverter;
import com.netcracker.odstc.logviewer.containers.AttributeObjectContainer;
import com.netcracker.odstc.logviewer.mapper.AttributeObjectMapper;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
@Repository
public class ContainerDAO extends EAVObjectDAO {
    public static final String ACTIVE_SERVER_DIRECTORY_FILES = "WITH active_servers AS (\n" +
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
            "                     WHERE LIST_VALUE_ID = 6\n" +
            "                       AND (nonactive.OBJECT_ID = directory.OBJECT_ID OR PARENT_ID = nonactive.OBJECT_ID)) -- IS ACTIVE)\n" +
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
            "                          WHERE LIST_VALUE_ID = 8\n" +
            "                            AND (nonactive.OBJECT_ID = directory.OBJECT_ID OR\n" +
            "                                 PARENT_ID = nonactive.OBJECT_ID)) -- IS ACTIVE\n" +
            "           AND PARENT_ID IN (SELECT OBJECT_ID FROM active_servers)) -- PARENT SERVER IS ACTIVE\n" +
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
            "  AND EXISTS(SELECT OBJECT_ID FROM active_directories WHERE OBJECT_ID = logfile.PARENT_ID)--PARENT IS ACTIVE\n" +
            "UNION\n" +
            "SELECT ATTR_ID,\n" +
            "       VALUE,\n" +
            "       DATE_VALUE,\n" +
            "       LIST_VALUE_ID,\n" +
            "       OBJECT_ID,\n" +
            "       PARENT_ID,\n" +
            "       OBJECT_TYPE_ID,\n" +
            "       NAME\n" +
            "FROM active_directories\n" +
            "UNION\n" +
            "SELECT *\n" +
            "FROM active_servers";//TODO: Сделать сортировку

    public ContainerDAO(JdbcTemplate jdbcTemplate) {
        super(jdbcTemplate);
    }

    public List<HierarchyContainer> getActiveElements(){
        List<AttributeObjectContainer> eavObjectsContainers = jdbcTemplate.query(ACTIVE_SERVER_DIRECTORY_FILES,new AttributeObjectMapper());

        Map<BigInteger,HierarchyContainer> hierarchyContainerMap = new AOCConverter().convertAOCtoHC(eavObjectsContainers);

        return new ArrayList<>(hierarchyContainerMap.values());
    }
}