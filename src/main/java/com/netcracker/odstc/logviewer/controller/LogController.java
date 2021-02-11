package com.netcracker.odstc.logviewer.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.netcracker.odstc.logviewer.containers.RuleContainer;
import com.netcracker.odstc.logviewer.containers.dto.LogDTO;
import com.netcracker.odstc.logviewer.models.Log;
import com.netcracker.odstc.logviewer.service.LogService;
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
import java.util.List;

@RequestMapping("api/log")
@RestController
public class LogController {
    private final Logger logger = LogManager.getLogger(LogController.class.getName());
    private static final String DEFAULT_PAGE_SIZE = "20";
    private final LogService logService;

    public LogController(LogService logService) {
        this.logService = logService;
    }

    @GetMapping("/directory")
    public Page<LogDTO> logs(@RequestParam String directoryId,
                             @RequestParam(value = "page", defaultValue = "1") int page,
                             @RequestParam(value = "pageSize", defaultValue = DEFAULT_PAGE_SIZE) int pageSize,
                             @RequestParam(value = "rule") String ruleString) throws JsonProcessingException {
        PageRequest pageable = PageRequest.of(page - 1, pageSize);// On UI pages starts from 1. Spring start count from 0.
        RuleContainer ruleContainer = new ObjectMapper().readValue(ruleString, RuleContainer.class);
        return logService.getLogsByDirectoryId(new BigInteger(directoryId), ruleContainer, pageable);
    }

    @GetMapping("/logFile")
    public Page<LogDTO> getLogByFileId(@RequestParam String logFileId,
                                       @RequestParam(value = "page", defaultValue = "1") int page,
                                       @RequestParam(value = "pageSize", defaultValue = DEFAULT_PAGE_SIZE) int pageSize,
                                       @RequestParam(value = "rule") String ruleString) throws JsonProcessingException {
        PageRequest pageable = PageRequest.of(page - 1, pageSize);// On UI pages starts from 1. Spring start count from 0.
        RuleContainer ruleContainer = new ObjectMapper().readValue(ruleString, RuleContainer.class);
        return logService.getLogsByFileId(new BigInteger(logFileId), ruleContainer, pageable);
    }

    @PostMapping("/add")
    public ResponseEntity<Log> add(@RequestBody Log log) {
        logger.info("Save log");
        logService.save(log);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<Log> deleteById(@PathVariable BigInteger id) {
        logger.info("Delete log");
        logService.deleteById(id);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/deletes/{ids}")
    public ResponseEntity<Log> deleteByIds(@PathVariable List<BigInteger> ids) {
        logger.info("Delete logs");
        logService.deleteByIds(ids);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Log> findById(@PathVariable BigInteger id) {
        return ResponseEntity.ok(logService.findById(id));
    }

    @GetMapping("/directory/count")
    public ResponseEntity<BigInteger> getLogsCountByDirectory(@RequestParam String directoryId,
                                                              @RequestParam(value = "rule") String ruleString) throws JsonProcessingException {
        RuleContainer ruleContainer = new ObjectMapper().readValue(ruleString, RuleContainer.class);
        return ResponseEntity.ok().body(logService.getCountByDirectory(new BigInteger(directoryId), ruleContainer));
    }

    @GetMapping("/logFile/count")
    public ResponseEntity<BigInteger> getLogsCountByFile(@RequestParam String fileId,
                                                         @RequestParam(value = "rule") String ruleString) throws JsonProcessingException {
        RuleContainer ruleContainer = new ObjectMapper().readValue(ruleString, RuleContainer.class);
        return ResponseEntity.ok().body(logService.getCountByLogFile(new BigInteger(fileId), ruleContainer));
    }

}
