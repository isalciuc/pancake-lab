package org.pancakelab.exception;

/**
 * Exception thrown when input validation fails.
 */
public class InvalidInputException extends RuntimeException {
    
    public InvalidInputException(String message) {
        super(message);
    }
    
    public InvalidInputException(String message, Throwable cause) {
        super(message, cause);
    }
}