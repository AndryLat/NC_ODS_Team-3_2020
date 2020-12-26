package com.netcracker.odstc.logviewer.controller;


import com.netcracker.odstc.logviewer.dao.EAVObjectDAO;
import com.netcracker.odstc.logviewer.models.Server;
import com.netcracker.odstc.logviewer.models.User;
import com.netcracker.odstc.logviewer.serverconnection.services.ServerConnectionService;
import com.netcracker.odstc.logviewer.service.ServerService;
import com.netcracker.odstc.logviewer.service.UserService;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigInteger;
import java.security.Principal;
import java.util.List;

@RequestMapping("/server")
@RestController
public class ServerController {

    private final ServerService serverService;
    private final UserService userService;
    private final EAVObjectDAO eavObjectDAO;

    public ServerController(ServerService serverService, UserService userService,
                            @Qualifier("EAVObjectDAO") EAVObjectDAO eavObjectDAO) {
        this.serverService = serverService;
        this.userService = userService;
        this.eavObjectDAO = eavObjectDAO;
    }


    @GetMapping("/all")
    public ResponseEntity<List<Server>> allServer(Principal principal){
        User user = userService.findByName(principal.getName());
        List<Server> listServer= eavObjectDAO.getObjectsByParentId(user.getObjectId(), Server.class);
        return ResponseEntity.ok(listServer);
    }

    @PostMapping("/add")
    public ResponseEntity<Server> add(@RequestBody Server server) {
        if (server == null) {
            return new ResponseEntity("Server shouldn't be null", HttpStatus.NOT_ACCEPTABLE);
        }
        serverService.save(server);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @PutMapping("/update")
    public ResponseEntity<Server> update(@RequestBody Server server) {
        if (server == null) {
            return new ResponseEntity("Server shouldn't be null", HttpStatus.NOT_ACCEPTABLE);
        }
        serverService.save(server);
        return new ResponseEntity(HttpStatus.OK);
    }

    @GetMapping("/id/{id}")
    public ResponseEntity<Server> findById(@PathVariable BigInteger id) {
        if (id == null || id.equals(BigInteger.valueOf(0))) {
            return new ResponseEntity("Id shouldn't be 0 or null", HttpStatus.NOT_ACCEPTABLE);
        }
        return ResponseEntity.ok(serverService.findById(id));
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<Server> deleteById(@PathVariable BigInteger id) {
        if (id == null || id.equals(BigInteger.valueOf(0))) {
            return new ResponseEntity("Id shouldn't be 0 or null", HttpStatus.NOT_ACCEPTABLE);
        }
        serverService.deleteById(id);
        return new ResponseEntity(HttpStatus.OK);
    }

    @PostMapping("/testConnection")
    public ResponseEntity<Boolean> testConnection(@RequestBody Server server){
        if(server == null){
            return new ResponseEntity("Server shouldn't be null", HttpStatus.NOT_ACCEPTABLE);
        }
        return  ResponseEntity.ok(ServerConnectionService.getInstance().isServerAvailable(server));
    }
}

