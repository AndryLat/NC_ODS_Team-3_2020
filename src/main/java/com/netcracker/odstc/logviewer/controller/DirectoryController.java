package com.netcracker.odstc.logviewer.controller;

import com.netcracker.odstc.logviewer.containers.DTO.DirectoryWithExtensionsDTO;
import com.netcracker.odstc.logviewer.models.Directory;
import com.netcracker.odstc.logviewer.models.LogFile;
import com.netcracker.odstc.logviewer.service.DirectoryService;
import com.netcracker.odstc.logviewer.service.LogFileService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
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
    private final Logger logger = LogManager.getLogger(DirectoryController.class);

    private final DirectoryService directoryService;
    private final LogFileService logFileService;

    public DirectoryController(DirectoryService directoryService,LogFileService logFileService) {
        this.directoryService = directoryService;
        this.logFileService = logFileService;
    }

    @GetMapping("/")
    public ResponseEntity<List<Directory>> all(@RequestParam BigInteger parentId) {
        logger.info("GET: Requested all directories by parentId {}", (parentId != null ? parentId : "null"));
        return ResponseEntity.ok(directoryService.findByParentId(parentId));
    }

    @PostMapping("/add")
    public ResponseEntity<Directory> add(@RequestBody Directory directory) {
        logger.info("POST: Requested save for directory with id {}", (directory.getObjectId() != null ? directory.getObjectId() : "null"));
        directoryService.add(directory);
        return ResponseEntity.ok(directory);
    }

    @PutMapping("/update")
    public ResponseEntity<Directory> update(@RequestBody Directory directory) {
        logger.info("PUT: Requested update for directory with id {}", (directory.getObjectId() != null ? directory.getObjectId() : "null"));
        directoryService.update(directory);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<Directory> deleteById(@PathVariable BigInteger id) {
        logger.info("DELETE: Requested deleting for directory id {}", (id != null ? id : "null"));
        directoryService.deleteById(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/id/{id}")
    public ResponseEntity<Directory> findById(@PathVariable BigInteger id) {
        logger.info("GET: Requested directory with id {}", (id != null ? id : "null"));
        return ResponseEntity.ok(directoryService.findById(id));
    }

    @GetMapping("/test")
    public ResponseEntity<Boolean> testConnection(@RequestBody Directory directory) {
        logger.info("GET: Requested test connection to directory");
        return ResponseEntity.ok(directoryService.testConnection(directory));
    }

    @GetMapping("/files")
    public ResponseEntity<List<LogFile>> getLogFilesFromDirectory(@RequestBody DirectoryWithExtensionsDTO directoryWithExtensionsDTO) {
        if(logger.isInfoEnabled()) {
            String directoryId = (directoryWithExtensionsDTO.getDirectory().getObjectId() != null ? String.valueOf(directoryWithExtensionsDTO.getDirectory().getObjectId()) : "null");
            logger.info("GET: Requested file list from directory with id {}", directoryId);
        }
        return ResponseEntity.ok(logFileService.getLogFileList(directoryWithExtensionsDTO));
    }

    @PostMapping("files/add")
    public ResponseEntity<Directory> addLogFiles(List<LogFile> logFiles) {
        if(logger.isInfoEnabled()) {
            String size = logFiles==null?"null array": String.valueOf(logFiles.size());
            logger.info("POST: Requested saving {} files", size);
        }
        logFileService.addLogFileList(logFiles);
        return ResponseEntity.noContent().build();
    }
}
