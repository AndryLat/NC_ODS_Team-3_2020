package com.netcracker.odstc.logviewer.controller;

import com.netcracker.odstc.logviewer.models.Directory;
import com.netcracker.odstc.logviewer.service.DirectoryService;
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

@RequestMapping("/directory")
@RestController
public class DirectoryController {
    private final DirectoryService directoryService;
    private String directoryNullMessage = "Directory shouldn't be 0 or null";
    private String directoryIdNullMessage = "Id shouldn't be 0 or null";

    public DirectoryController(DirectoryService directoryService) {
        this.directoryService = directoryService;
    }


    @PostMapping("/add")
    public ResponseEntity<Directory> add(@RequestBody Directory directory) {
        if (directory == null) {
            return new ResponseEntity(directoryNullMessage, HttpStatus.NOT_ACCEPTABLE);
        }
        directoryService.save(directory);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @PutMapping("/update")
    public ResponseEntity<Directory> update(@RequestBody Directory directory) {
        if (directory == null) {
            return new ResponseEntity(directoryNullMessage, HttpStatus.NOT_ACCEPTABLE);
        }
        directoryService.save(directory);
        return new ResponseEntity(HttpStatus.OK);
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<Directory> deleteById(@PathVariable BigInteger id) {
        if (id == null || id.equals(BigInteger.valueOf(0))) {
            return new ResponseEntity(directoryNullMessage, HttpStatus.NOT_ACCEPTABLE);
        }
        directoryService.deleteById(id);
        return new ResponseEntity(HttpStatus.OK);
    }

    @GetMapping("/id/{id}")
    public ResponseEntity<Directory> findById(@PathVariable BigInteger id) {
        if (id == null || id.equals(BigInteger.valueOf(0))) {
            return new ResponseEntity(directoryIdNullMessage, HttpStatus.NOT_ACCEPTABLE);
        }
        return ResponseEntity.ok(directoryService.findById(id));
    }


}
