package com.netcracker.odstc.logviewer.dao;

import com.netcracker.odstc.logviewer.mapper.ServerMapper;
import com.netcracker.odstc.logviewer.models.Server;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.math.BigInteger;
import java.util.List;

@Repository
public class ServerDAO implements DAO<Server>{

    private final JdbcTemplate jdbcTemplate;
    private final String GET_ALL_SERVER_QUERY = "SELECT OBJECT_ID  from OBJECTS where  object_type_id = 2;";
    private final String GET_SERVER_BY_ID_QUERY = "select OBJECT_ID from OBJECTS where  object_id = ? and  object_type_id = 2;";
    private final String DELETE_QUERY = "DELETE FROM OBJECTS  WHERE object_id = ?;";

    public ServerDAO(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public void save(Server server) {
        server.saveToDB();
    }

    @Override
    public void update(Server server) { server.saveToDB(); }

    @Override
    public void deleteById(BigInteger id) {
        jdbcTemplate.update(DELETE_QUERY, id);
    }

    @Override
    public List<Server> getAll() {
        return jdbcTemplate.query(GET_ALL_SERVER_QUERY, new ServerMapper());

    }

    @Override
    public Server get(BigInteger id) {
        return jdbcTemplate.queryForObject(GET_SERVER_BY_ID_QUERY, new ServerMapper(), id);
    }
}



