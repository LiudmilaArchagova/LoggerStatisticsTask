package org.example.exception;

public class CommonIOException extends RuntimeException{
    public CommonIOException(String message) {
        super(message);
    }

    public CommonIOException(String message, Throwable cause) {
        super(message, cause);
    }
}
