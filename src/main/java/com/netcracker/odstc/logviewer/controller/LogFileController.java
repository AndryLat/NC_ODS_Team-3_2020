package com.netcracker.odstc.logviewer.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.netcracker.odstc.logviewer.models.Directory;
import com.netcracker.odstc.logviewer.models.LogFile;
import com.netcracker.odstc.logviewer.service.DirectoryService;
import com.netcracker.odstc.logviewer.service.LogFileService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigInteger;
import java.util.List;

@RequestMapping("api/logFile")
@RestController
public class LogFileController {
    private final Logger logger = LogManager.getLogger(LogFileController.class);
    private static final String DEFAULT_PAGE_SIZE = "10";
    private static final String LOG_FILE_NULL_MESSAGE = "File of logs shouldn't be 0 or null";
    private final DirectoryService directoryService;
    private final LogFileService logFileService;

    public LogFileController(DirectoryService directoryService, LogFileService logFileService) {
        this.directoryService = directoryService;
        this.logFileService = logFileService;
    }

    @GetMapping("/")
    public Page<LogFile> showAllLogFiles(@RequestParam String directoryId,
                                         @RequestParam(value = "page", defaultValue = "0") int page,
                                         @RequestParam(value = "pageSize", defaultValue = DEFAULT_PAGE_SIZE) int pageSize) {
        PageRequest pageRequest = PageRequest.of(page - 1, pageSize);
        Directory directory = directoryService.findById(new BigInteger(directoryId));
        return logFileService.getLogFileListByPage(pageRequest, directory);
    }

    @GetMapping("/files")
    public ResponseEntity<List<LogFile>> getLogFilesFromDirectory(@RequestParam String directoryInString) throws JsonProcessingException {
        logger.info("GET: Requested file listing from directory");
        Directory directory = new ObjectMapper().readValue(directoryInString, Directory.class);
        return ResponseEntity.ok(logFileService.getLogFileList(directory));
    }

    @GetMapping("/files/database")
    public ResponseEntity<List<LogFile>> getLogFilesFromDirectoryFromDB(@RequestParam BigInteger objectId) {
        logger.info("GET: Requested file listing from database with directory id {}", (objectId != null ? objectId : "null"));
        return ResponseEntity.ok(logFileService.getLogFileListFromDB(objectId));
    }

    @GetMapping("/{id}")
    public ResponseEntity<LogFile> getLogFileById(@PathVariable BigInteger id) {
        logger.info("GET: Requested file of logs with id {}", (id != null ? id : "null"));
        return ResponseEntity.ok(logFileService.findById(id));
    }

    @PostMapping("/file/add")
    public ResponseEntity<BigInteger> addLogFile(@RequestBody LogFile logFile) {
        if (logFile == null) {
            throwException(LOG_FILE_NULL_MESSAGE);
        }
        logFileService.addLogFile(logFile);
        return ResponseEntity.ok(logFile.getObjectId());
    }

    @PostMapping("/files/add")
    public ResponseEntity<List<LogFile>> addLogFiles(@RequestBody List<LogFile> logFiles) {
        for (LogFile logFile : logFiles) {
            if (logFile == null) {
                throwException(LOG_FILE_NULL_MESSAGE);
            }
        }
        logFileService.addLogFileList(logFiles);
        return ResponseEntity.ok(logFiles);
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<LogFile> deleteById(@PathVariable BigInteger id) {
        logFileService.deleteById(id);
        logger.info("File of logs with id: {} deleted", id);
        return ResponseEntity.noContent().build();
    }

    private void throwException(String nameException) {
        IllegalArgumentException exception = new IllegalArgumentException();
        logger.error(nameException);
        throw exception;
    }
}
