package com.netcracker.odstc.logviewer.controller;


import com.netcracker.odstc.logviewer.models.Server;
import com.netcracker.odstc.logviewer.service.ServerService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigInteger;

@RequestMapping("/server")
@RestController
public class ServerController {

    private final ServerService serverService;

    public ServerController(ServerService serverService) {
        this.serverService = serverService;
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
        if (id == null || id.equals(BigInteger.valueOf(0))){
           return new ResponseEntity("Id shouldn't be 0 or null",HttpStatus.NOT_ACCEPTABLE);
        }
        return ResponseEntity.ok(serverService.findById(id));
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<Server> deleteById(@PathVariable BigInteger id){
        if (id == null || id.equals(BigInteger.valueOf(0))){
            return new ResponseEntity("Id shouldn't be 0 or null",HttpStatus.NOT_ACCEPTABLE);
        }
        serverService.deleteById(id);
        return new ResponseEntity(HttpStatus.OK);
    }

}

