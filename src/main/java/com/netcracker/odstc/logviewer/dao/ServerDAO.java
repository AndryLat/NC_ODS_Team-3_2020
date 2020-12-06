package com.netcracker.odstc.logviewer.dao;

import com.netcracker.odstc.logviewer.mapper.ServerMapper;
import com.netcracker.odstc.logviewer.models.Server;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.math.BigInteger;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

@Repository
public class ServerDAO {

    private final JdbcTemplate jdbcTemplate;

    public ServerDAO(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public void save(Server server) {
        server.saveToDB();
    }

    public List<Server> findAll() {
        String sql = "select OBJECT_ID " +
                "from OBJECTS" +
                " where  object_type_id = ?;";
        return jdbcTemplate.query(sql, new ServerMapper());

    }

    public Server findById(BigInteger id) {
        String sql = "select OBJECT_ID \n" +
                "from OBJECTS where  object_id = ?;";
        return jdbcTemplate.queryForObject(sql, new ServerMapper(), id);
    }


    public List<Server> findByIpAddress(String ipAddress) {
        String sql = "select obj.OBJECT_ID from OBJECTS obj, attributes ip_address1 " +
                "where  ip_address1.value like ? " +
                "and  ip_address1.attr_id = 6 and obj.object_id = ip_address1.object_id; ";
        return jdbcTemplate.query(sql, new ServerMapper(), ipAddress);
    }

    public Server findByLogin(String login) {
        String sql = "select obj.OBJECT_ID from OBJECTS obj, attributes login1 " +
                "where  login1.value like ? " +
                "and  login1.attr_id = 7 and obj.object_id = login1.object_id; ";
        return jdbcTemplate.queryForObject(sql, new ServerMapper(), login);
    }

    public List<Server> findByProtocol(String protocol) {
        String sql = "select obj.OBJECT_ID from OBJECTS obj, attributes protocol1, lists protocols\n" +
                "where  protocols.value like ? \n" +
                "and  protocol1.attr_id = 9 \n" +
                "and protocols.list_value_id = protocol1.list_value_id\n" +
                "and obj.object_id = protocol1.object_id; ";
        return jdbcTemplate.query(sql, new ServerMapper(), protocol);
    }

    public List<Server> findByPort(int port) {
        String sql = "select obj.OBJECT_ID from OBJECTS obj, attributes port1\n" +
                "where  port1.value = ? \n" +
                "and  port1.attr_id = 10 \n" +
                "and obj.object_id = port1.object_id; ";
        return jdbcTemplate.query(sql, new ServerMapper(), port);
    }

    public List<Server> findByDateLastAccessByJob(Date lastAccessByJob) {
        DateFormat dateFormat = new SimpleDateFormat("dd-M-yyyy  hh:mm:ss");
        String strDate = dateFormat.format(lastAccessByJob);
        String sql = "SELECT obj.OBJECT_ID FROM OBJECTS obj, attributes last_access_by_job\n" +
                "where last_access_by_job.date_value = to_date(?)\n" +
                "and last_access_by_job.attr_id = 12\n" +
                "and obj.object_id = last_access_by_job.object_id;";
        return jdbcTemplate.query(sql, new ServerMapper(), strDate);
    }

    public List<Server> findByDateLastAccessByUser(Date lastAccessByUser) {
        DateFormat dateFormat = new SimpleDateFormat("dd-M-yyyy  hh:mm:ss");
        String strDate = dateFormat.format(lastAccessByUser);
        String sql = "SELECT obj.OBJECT_ID FROM OBJECTS obj, attributes last_access_by_user\n" +
                "where last_access_by_user.date_value = to_date(?)\n" +
                "and last_access_by_user.attr_id = 13\n" +
                "and obj.object_id = last_access_by_user.object_id;";
        return jdbcTemplate.query(sql, new ServerMapper(), strDate);
    }

    public void deleteById(long id) {
        String sql = "DELETE FROM OBJECTS  WHERE object_id = ?;";
        jdbcTemplate.update(sql, id);
    }
}



