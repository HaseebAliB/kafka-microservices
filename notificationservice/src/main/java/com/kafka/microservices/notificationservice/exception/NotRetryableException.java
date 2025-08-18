package com.kafka.microservices.notificationservice.exception;

public class NotRetryableException extends RuntimeException {

    public NotRetryableException(String message) {
        super(message);
        // TODO Auto-generated constructor stub
    }

    public NotRetryableException(Throwable cause) {
        super(cause);
        // TODO Auto-generated constructor stub
    }
}
