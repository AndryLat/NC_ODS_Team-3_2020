package com.netcracker.odstc.logviewer.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.netcracker.odstc.logviewer.models.Directory;
import com.netcracker.odstc.logviewer.service.DirectoryService;
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

@RequestMapping("api/directory")
@RestController
public class DirectoryController {
    private static final Logger logger = LogManager.getLogger(DirectoryController.class);
    private static final String DEFAULT_PAGE_SIZE = "10";
    private final DirectoryService directoryService;

    public DirectoryController(DirectoryService directoryService) {
        this.directoryService = directoryService;
    }

    @GetMapping("/")
    public ResponseEntity<Page<Directory>> getDirectoriesByParentId(@RequestParam BigInteger parentId,
                                                                    @RequestParam(value = "page", defaultValue = "1") int page,
                                                                    @RequestParam(value = "pageSize", defaultValue = DEFAULT_PAGE_SIZE) int pageSize) {
        Pageable pageable = PageRequest.of(page - 1, pageSize);
        logger.debug("GET: Requested all directories by parentId {}", (parentId != null ? parentId : "null"));
        return ResponseEntity.ok(directoryService.findByParentId(parentId, pageable));
    }

    @PostMapping("/add")
    public ResponseEntity<Directory> addDirectory(@RequestBody Directory directory) {
        logger.debug("POST: Requested save for directory with id {}", (directory.getObjectId() != null ? directory.getObjectId() : "null"));
        directoryService.add(directory);
        return ResponseEntity.ok(directory);
    }

    @PutMapping("/update")
    public ResponseEntity<Directory> updateDirectory(@RequestBody Directory directory) {
        logger.debug("PUT: Requested update for directory with id {}", (directory.getObjectId() != null ? directory.getObjectId() : "null"));
        directoryService.update(directory);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/updateAttributes")
    public ResponseEntity<Directory> updateDirectoryAttributes(@RequestBody Directory directory) {
        logger.info("PUT: Requested attributes update for directory with id {}", (directory.getObjectId() != null ? directory.getObjectId() : "null"));
        directoryService.update(directory);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<Directory> deleteDirectoryById(@PathVariable BigInteger id) {
        logger.debug("DELETE: Requested deleting for directory id {}", (id != null ? id : "null"));
        directoryService.deleteById(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Directory> getDirectoryById(@PathVariable BigInteger id) {
        logger.debug("GET: Requested directory with id {}", (id != null ? id : "null"));
        return ResponseEntity.ok(directoryService.findById(id));
    }

    @GetMapping("/test")
    public ResponseEntity<Boolean> testConnectionToDirectory(@RequestParam String directoryInString) throws JsonProcessingException {
        Directory directory = new ObjectMapper().readValue(directoryInString, Directory.class);
        if(logger.isDebugEnabled()) {
            if(directory!=null){
                String path = "[null path]";
                String parentId = "[null parentId]";
                if(directory.getPath()!=null){
                    path = directory.getPath();
                }
                if(directory.getParentId()!=null){
                    parentId = directory.getParentId().toString();
                }
                logger.debug("GET: Requested test connection to directory {}. From server with Id: {}",path,parentId);
            }else{
                logger.debug("GET: Requested test connection to directory [null]");
            }

        }
        return ResponseEntity.ok(directoryService.testConnection(directory));
    }
}
