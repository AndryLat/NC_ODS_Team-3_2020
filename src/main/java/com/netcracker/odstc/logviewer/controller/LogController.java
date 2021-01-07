package com.netcracker.odstc.logviewer.controller;

import com.netcracker.odstc.logviewer.dao.LogDAO;
import com.netcracker.odstc.logviewer.models.Log;
import com.netcracker.odstc.logviewer.service.LogService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigInteger;

@RequestMapping("/log")
@RestController

public class LogController {
    private LogService logService;
    LogDAO logDAO = new LogDAO(new JdbcTemplate());
    private String logNullMessage = "Log shouldn't be 0 or null";
    private String logIdNullMessage = "Log shouldn't be 0 or null";

    public LogController(LogService logService) {
        this.logService = logService;
    }


    @PostMapping("/add")
    public ResponseEntity<Log> add(@RequestBody Log log) {
        if (log == null) {
            return new ResponseEntity(logNullMessage, HttpStatus.NOT_ACCEPTABLE);
        }
        logService.save(log);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @PutMapping("/update")
    public ResponseEntity<Log> update(@RequestBody  Log log) {
        if (log == null) {
            return new ResponseEntity(logNullMessage, HttpStatus.NOT_ACCEPTABLE);
        }
        logService.save(log);
        return new ResponseEntity(HttpStatus.OK);
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<Log> deleteById(@PathVariable BigInteger id) {
        if (id == null || id.equals(BigInteger.valueOf(0))) {
            return new ResponseEntity(logNullMessage, HttpStatus.NOT_ACCEPTABLE);
        }
        logService.deleteById(logDAO.getById(id));
        return new ResponseEntity(HttpStatus.OK);
    }

    @GetMapping("/id/{id}")
    public ResponseEntity<Log> findById(@PathVariable BigInteger id) {
        if (id == null || id.equals(BigInteger.valueOf(0))) {
            return new ResponseEntity(logNullMessage, HttpStatus.NOT_ACCEPTABLE);
        }
        return ResponseEntity.ok(logService.findById(id));
    }
}
