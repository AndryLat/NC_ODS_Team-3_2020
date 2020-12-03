package com.netcracker.odstc.logviewer.serverconnection.exceptions;

/**
 * Description:
 *
 * @author Aleksanid
 * created 03.12.2020
 */
public class ServerLogProcessingException extends RuntimeException {
    public ServerLogProcessingException(String message){
        super(message);
    }
    public ServerLogProcessingException(Exception exception){
        super(exception.getMessage(),exception.getCause());
    }
}
