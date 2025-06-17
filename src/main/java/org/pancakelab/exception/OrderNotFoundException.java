package org.pancakelab.exception;

import java.util.UUID;

/**
 * Exception thrown when an order cannot be found
 */
public class OrderNotFoundException extends RuntimeException {
    
    public OrderNotFoundException(UUID orderId) {
        super("Order not found: " + orderId);
    }
    
    public OrderNotFoundException(String message) {
        super(message);
    }
}