package com.netcracker.odstc.logviewer.controller;

import com.netcracker.odstc.logviewer.dao.LogDAO;
import com.netcracker.odstc.logviewer.models.Log;
import com.netcracker.odstc.logviewer.service.LogService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
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
    private static final String logNullMessage = "Log shouldn't be 0 or null";
    private static final String logIdNullMessage = "Log shouldn't be 0 or null";
    private final Logger logger = LogManager.getLogger(LogController.class.getName());

    public LogController(LogService logService) {
        this.logService = logService;
    }

    @PostMapping("/add")
    public ResponseEntity<Log> add(@RequestBody Log log) {
        if (log == null) {
            throwException(logNullMessage);
        }
        logger.info("Save log");
        logService.save(log);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @PutMapping("/update")
    public ResponseEntity<Log> update(@RequestBody Log log) {
        if (log == null) {
            throwException(logNullMessage);
        }
        logger.info("Update log");
        logService.save(log);
        return new ResponseEntity(HttpStatus.OK);
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<Log> deleteById(@PathVariable BigInteger id) {
        if (id == null || id.equals(BigInteger.valueOf(0))) {
            throwException(logIdNullMessage);
        }
        logger.info("Delete log");
        logService.deleteById(logDAO.getById(id));
        return new ResponseEntity(HttpStatus.OK);
    }

    @GetMapping("/id/{id}")
    public ResponseEntity<Log> findById(@PathVariable BigInteger id) {
        if (id == null || id.equals(BigInteger.valueOf(0))) {
            throwException(logIdNullMessage);
        }
        return ResponseEntity.ok(logService.findById(id));
    }

    private void throwException(String nameException) {
        IllegalArgumentException exception = new IllegalArgumentException();
        logger.error(nameException);
        throw exception;
    }
}
