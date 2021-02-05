package com.netcracker.odstc.logviewer.restexception;

import com.fasterxml.jackson.databind.JsonMappingException;
import com.netcracker.odstc.logviewer.service.exceptions.DirectoryServiceException;
import com.netcracker.odstc.logviewer.service.exceptions.LogFileServiceException;
import com.netcracker.odstc.logviewer.service.exceptions.LogServiceException;
import com.netcracker.odstc.logviewer.service.exceptions.ServerServiceException;
import com.netcracker.odstc.logviewer.service.exceptions.UserServiceException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.time.ZoneId;
import java.time.ZonedDateTime;

import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.NOT_FOUND;

@ControllerAdvice
public class ApiExceptionHandler {

    private final Logger logger = LogManager.getLogger(ApiExceptionHandler.class.getName());

    @ExceptionHandler(value = {JsonMappingException.class,
            DirectoryServiceException.class,
            LogServiceException.class,
            LogFileServiceException.class,
            ServerServiceException.class,
            UserServiceException.class
    })
    public ResponseEntity<Object> handleApiRequestException(RuntimeException ex) {
        ApiErrorMessage apiErrorMessage = new ApiErrorMessage(
                ex.getMessage(),
                ex,
                BAD_REQUEST,
                ZonedDateTime.now(ZoneId.of("Z")));
        logger.error("Handled BAD_REQUEST with exception", ex);
        return new ResponseEntity<>(apiErrorMessage, BAD_REQUEST);
    }

    @ExceptionHandler(value = {IllegalArgumentException.class})
    public ResponseEntity<Object> exception(IllegalArgumentException ex) {
        ApiErrorMessage apiErrorMessage = new ApiErrorMessage(
                ex.getMessage(),
                ex,
                NOT_FOUND,
                ZonedDateTime.now(ZoneId.of("Z")));
        logger.error("Handled NOT_FOUND with exception", ex);
        return new ResponseEntity<>(apiErrorMessage, NOT_FOUND);
    }
}
