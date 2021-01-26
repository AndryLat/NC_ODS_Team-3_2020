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
import org.springframework.data.domain.Pageable;
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
import java.util.List;

@RequestMapping("api/directory")
@RestController
public class DirectoryController {
    private final static Logger logger = LogManager.getLogger(DirectoryController.class);
    private static final String DEFAULT_PAGE_SIZE = "10";
    private final DirectoryService directoryService;
    private final LogFileService logFileService;

    public DirectoryController(DirectoryService directoryService, LogFileService logFileService) {
        this.directoryService = directoryService;
        this.logFileService = logFileService;
    }

    @GetMapping("/")
    public ResponseEntity<Page<Directory>> getDirectoriesByParentId(@RequestParam BigInteger parentId,
                                                                    @RequestParam(value = "page", defaultValue = "1") int page,
                                                                    @RequestParam(value = "pageSize", defaultValue = DEFAULT_PAGE_SIZE) int pageSize) {
        Pageable pageable = PageRequest.of(page - 1, pageSize);
        logger.info("GET: Requested all directories by parentId {}", (parentId != null ? parentId : "null"));
        return ResponseEntity.ok(directoryService.findByParentId(parentId, pageable));
    }

    @PostMapping("/add")
    public ResponseEntity<Directory> addDirectory(@RequestBody Directory directory) {
        logger.info("POST: Requested save for directory with id {}", (directory.getObjectId() != null ? directory.getObjectId() : "null"));
        directoryService.add(directory);
        return ResponseEntity.ok(directory);
    }

    @PutMapping("/update")
    public ResponseEntity<Directory> updateDirectory(@RequestBody Directory directory) {
        logger.info("PUT: Requested update for directory with id {}", (directory.getObjectId() != null ? directory.getObjectId() : "null"));
        directoryService.update(directory);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<Directory> deleteDirectoryById(@PathVariable BigInteger id) {
        logger.info("DELETE: Requested deleting for directory id {}", (id != null ? id : "null"));
        directoryService.deleteById(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Directory> getDirectoryById(@PathVariable BigInteger id) {
        logger.info("GET: Requested directory with id {}", (id != null ? id : "null"));
        return ResponseEntity.ok(directoryService.findById(id));
    }

    @GetMapping("/test")
    public ResponseEntity<Boolean> testConnectionToDirectory(@RequestParam String directoryInString) throws JsonProcessingException {
        logger.info("GET: Requested test connection to directory");
        Directory directory = new ObjectMapper().readValue(directoryInString,Directory.class);
        Directory dir = new Directory(directory.getPath());
        dir.setParentId(directory.getParentId());
        return ResponseEntity.ok(directoryService.testConnection(dir));
    }

    @GetMapping("/files")
    public ResponseEntity<List<LogFile>> getLogFilesFromDirectory(@RequestParam String directoryInString) throws JsonProcessingException {
        logger.info("GET: Requested file listing from directory");
        Directory directory = new ObjectMapper().readValue(directoryInString,Directory.class);
        return ResponseEntity.ok(logFileService.getLogFileList(directory));
    }

    @PostMapping("/files/add")
    public ResponseEntity<Directory> addLogFiles(@RequestBody List<LogFile> logFiles) {
        if (logger.isInfoEnabled()) {
            String size = logFiles == null ? "null array" : String.valueOf(logFiles.size());
            logger.info("POST: Requested saving {} files", size);
        }
        logFileService.addLogFileList(logFiles);
        return ResponseEntity.noContent().build();
    }
}
