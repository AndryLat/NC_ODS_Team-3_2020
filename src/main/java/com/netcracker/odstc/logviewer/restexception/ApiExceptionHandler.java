package com.netcracker.odstc.logviewer.restexception;

import com.fasterxml.jackson.databind.JsonMappingException;
import static org.springframework.http.HttpStatus.*;

import com.netcracker.odstc.logviewer.service.exceptions.DirectoryServiceException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.time.ZoneId;
import java.time.ZonedDateTime;

@ControllerAdvice
public class ApiExceptionHandler {

    @ExceptionHandler(value = {ApiRequestException.class,
            JsonMappingException.class,
            DirectoryServiceException.class
    })
    public ResponseEntity<Object> handleApiRequestException(ApiRequestException ex) {
        //1.Create payload containing exception
        ApiException apiException = new ApiException(
                ex.getMessage(),
                ex,
                BAD_REQUEST,
                ZonedDateTime.now(ZoneId.of("Z")));
        //2.Return response entity
        return new ResponseEntity<>(apiException, BAD_REQUEST);
    }

    @ExceptionHandler(value = {IllegalArgumentException.class})
    public ResponseEntity<Object> exception(IllegalArgumentException ex){
        ApiException apiException = new ApiException(
                ex.getMessage(),
                ex,
                NOT_FOUND,
                ZonedDateTime.now(ZoneId.of("Z")));
        return new ResponseEntity<>(apiException, NOT_FOUND);
    }
}
