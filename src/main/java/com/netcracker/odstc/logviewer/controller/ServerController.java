package com.netcracker.odstc.logviewer.controller;

import com.netcracker.odstc.logviewer.models.Server;
import com.netcracker.odstc.logviewer.models.User;
import com.netcracker.odstc.logviewer.serverconnection.services.ServerConnectionService;
import com.netcracker.odstc.logviewer.service.ServerService;
import com.netcracker.odstc.logviewer.service.UserService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigInteger;
import java.security.Principal;

@RequestMapping("/api/server")
@RestController
public class ServerController {
    private static final Logger logger = LogManager.getLogger(ServerController.class.getName());
    private static final String DEFAULT_PAGE_SIZE = "10";
    private final ServerService serverService;
    private final UserService userService;

    public ServerController(ServerService serverService, UserService userService) {
        this.serverService = serverService;
        this.userService = userService;
    }

    @GetMapping("/")
    public Page<Server> showAllServers(Principal principal,
                                       @RequestParam(value = "page", defaultValue = "0") int page,
                                       @RequestParam(value = "pageSize", defaultValue = DEFAULT_PAGE_SIZE) int pageSize) {
        PageRequest pageRequest = PageRequest.of(page - 1, pageSize);
        User user = userService.findByLogin(principal.getName());
        logger.info("GET: Request to get all servers by user");
        return serverService.showAllServersByPagination(pageRequest, user);
    }

    @PostMapping("/add")
    public ResponseEntity<BigInteger> add(@RequestBody Server server, Principal principal) {
        User user = userService.findByLogin(principal.getName());
        serverService.add(server, user.getObjectId());
        logger.info("POST: Request adding a new server with id {}", (server.getObjectId() != null ? server.getObjectId(): "null"));
        return ResponseEntity.ok(server.getObjectId());
    }

    @PutMapping("/update")
    public ResponseEntity<Server> update(@RequestBody Server server) {
        serverService.update(server);
        logger.debug("PUT: Request updating for server id {}", (server.getObjectId() != null ? server.getObjectId(): "null"));
        return ResponseEntity.accepted().build();
    }

    @PutMapping("/updateLastAccessByUser")
    public ResponseEntity<Server> updateLastAccessByJob(@RequestBody Server server) {
        serverService.updateLastAccessByUser(server);
        logger.info("PUT: Request to update the server with last access by job for id {}", (server.getObjectId() != null ? server.getObjectId(): "null"));
        return ResponseEntity.accepted().build();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Server> findById(@PathVariable BigInteger id) {
        Server server = serverService.findById(id);
        logger.info("GET:Request to get server by id {}", (id != null ? id: "null"));
        return ResponseEntity.ok(server);
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<Server> deleteById(@PathVariable BigInteger id) {
        serverService.deleteById(id);
        logger.info("DELETE: Request to delete server by id {}", (id != null ? id: "null"));
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/testConnection")
    public ResponseEntity<Boolean> testConnection(@RequestBody Server server) {
        return ResponseEntity.ok(ServerConnectionService.getInstance().isServerAvailable(server));
    }

}

