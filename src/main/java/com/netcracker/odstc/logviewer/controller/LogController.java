package com.netcracker.odstc.logviewer.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.netcracker.odstc.logviewer.containers.dto.LogDTO;
import com.netcracker.odstc.logviewer.models.Log;
import com.netcracker.odstc.logviewer.service.LogService;
import com.netcracker.odstc.logviewer.service.RuleContainer;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigInteger;

@RequestMapping("api/log")
@RestController
public class LogController {
    private final Logger logger = LogManager.getLogger(LogController.class.getName());
    private static final String DEFAULT_PAGE_SIZE = "20";
    private static final String logNullMessage = "Log shouldn't be 0 or null";
    private static final String logIdNullMessage = "Log shouldn't be 0 or null";

    private LogService logService;

    public LogController(LogService logService) {
        this.logService = logService;
    }

    @GetMapping("/")
    public Page<LogDTO> logs(@RequestParam String directoryId,
                             @RequestParam(value = "page", defaultValue = "1") int page,
                             @RequestParam(value = "pageSize", defaultValue = DEFAULT_PAGE_SIZE) int pageSize,
                             @RequestParam(value = "rule") String ruleString) throws JsonProcessingException {
        PageRequest pageable = PageRequest.of(page-1, pageSize);// On UI pages starts from 1. Spring start count from 0.
        RuleContainer ruleContainer = new ObjectMapper().readValue(ruleString,RuleContainer.class);
        return logService.getAllLogsByAllValues(new BigInteger(directoryId),ruleContainer,pageable);
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

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<Log> deleteById(@PathVariable BigInteger id) {
        if (id == null || id.equals(BigInteger.valueOf(0))) {
            throwException(logIdNullMessage);
        }
        logger.info("Delete log");
        logService.deleteById(id);
        return ResponseEntity.noContent().build();
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
