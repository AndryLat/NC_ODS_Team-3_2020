package com.netcracker.odstc.logviewer.controller;

import com.netcracker.odstc.logviewer.dao.EAVObjectDAO;
import com.netcracker.odstc.logviewer.models.Server;
import com.netcracker.odstc.logviewer.models.User;
import com.netcracker.odstc.logviewer.serverconnection.services.ServerConnectionService;
import com.netcracker.odstc.logviewer.service.ServerService;
import com.netcracker.odstc.logviewer.service.UserService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigInteger;
import java.security.Principal;
import java.util.List;

@RequestMapping("/api/server")
@RestController
public class ServerController {

    private final ServerService serverService;
    private final UserService userService;
    private final EAVObjectDAO eavObjectDAO;
    private static final String serverNotNull = "Server shouldn't be null";
    private static final String idNotNull = "Id shouldn't be 0 or null";
    private static final String DEFAULT_PAGE_SIZE = "10";
    private final Logger logger = LogManager.getLogger(ServerController.class.getName());

    public ServerController(ServerService serverService, UserService userService, EAVObjectDAO eavObjectDAO) {
        this.serverService = serverService;
        this.userService = userService;
        this.eavObjectDAO = eavObjectDAO;
    }

    @GetMapping("/")
    public Page<Server> showAllServers(Principal principal,
                                                       @RequestParam (value = "page", defaultValue = "0") int page,
                                                       @RequestParam(value = "pageSize", defaultValue = DEFAULT_PAGE_SIZE) int pageSize) {
        PageRequest pageRequest = PageRequest.of(page, pageSize);
        User user = userService.findByLogin(principal.getName());
        List<Server> serve = eavObjectDAO.getObjectsByParentId(pageRequest,user.getObjectId(), Server.class);
       return new PageImpl<>(serve);
    }

    @PostMapping("/add")
    public ResponseEntity<BigInteger> add(@RequestBody Server server, Principal principal) {
        if (server == null) {
            throwException(serverNotNull);
        }

        User user = userService.findByLogin(principal.getName());
        serverService.add(server, user.getObjectId());
        logger.info("Server added");
        return ResponseEntity.ok(server.getObjectId());
    }

    @PutMapping("/update")
    public ResponseEntity<Server> update(@RequestBody Server server) {
        if (server == null) {
            throwException(serverNotNull);
        }
        serverService.update(server);
        logger.info("Server update");
        return new ResponseEntity(HttpStatus.OK);
    }

    @GetMapping("/id/{id}")
    public ResponseEntity<Server> findById(@PathVariable BigInteger id) {
        if (id == null || id.equals(BigInteger.valueOf(0))) {
            throwException(idNotNull);
        }
        return ResponseEntity.ok(serverService.findById(id));
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<Server> deleteById(@PathVariable BigInteger id) {
        if (id == null || id.equals(BigInteger.valueOf(0))) {
            throwException(idNotNull);
        }
        serverService.deleteById(id);
        logger.info("Server delete");
        return new ResponseEntity(HttpStatus.OK);
    }

    @PostMapping("/testConnection")
    public ResponseEntity<Boolean> testConnection(@RequestBody Server server) {
        if (server == null) {
            throwException(serverNotNull);
        }
        return ResponseEntity.ok(ServerConnectionService.getInstance().isServerAvailable(server));
    }

    private void throwException(String nameException) {
        IllegalArgumentException exception = new IllegalArgumentException();
        logger.error(nameException);
        throw exception;
    }
}

