package com.netcracker.odstc.logviewer.restexception;

import org.springframework.http.HttpStatus;

import java.time.ZonedDateTime;

public class ApiErrorMessage {
    private final String message;
    private final Throwable throwable;
    private final HttpStatus status;
    private final ZonedDateTime timestamp;

    public ApiErrorMessage(String message,
                           Throwable throwable,
                           HttpStatus status,
                           ZonedDateTime timestamp) {
        this.message = message;
        this.throwable = throwable;
        this.status = status;
        this.timestamp = timestamp;
    }

    public String getMessage() {
        return message;
    }

    public Throwable getThrowable() {
        return throwable;
    }

    public HttpStatus getStatus() {
        return status;
    }

    public ZonedDateTime getTimestamp() {
        return timestamp;
    }

    @Override
    public String toString() {
        return "ApiErrorMessage{" +
                "message='" + message + '\'' +
                ", throwable=" + throwable +
                ", status=" + status +
                ", timestamp=" + timestamp +
                '}';
    }
}
