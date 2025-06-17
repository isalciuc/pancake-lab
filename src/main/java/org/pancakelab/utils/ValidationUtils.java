package org.pancakelab.utils;

import org.pancakelab.exception.InvalidInputException;

import java.util.UUID;

/**
 * Utility class for input validation to prevent errors and improve system stability.
 */
public class ValidationUtils {
    
    // Constants for validation
    private static final int MIN_BUILDING_NUMBER = 1;
    private static final int MAX_BUILDING_NUMBER = 100;
    private static final int MIN_ROOM_NUMBER = 1;
    private static final int MAX_ROOM_NUMBER = 999;

    private ValidationUtils() {
        // Private constructor to prevent instantiation
    }
    
    /**
     * Validates building number
     * @param building the building number to validate
     * @throws InvalidInputException if building number is invalid
     */
    public static void validateBuilding(int building) {
        if (building < MIN_BUILDING_NUMBER || building > MAX_BUILDING_NUMBER) {
            throw new InvalidInputException("Building number must be between " + 
                MIN_BUILDING_NUMBER + " and " + MAX_BUILDING_NUMBER);
        }
    }
    
    /**
     * Validates room number
     * @param room the room number to validate
     * @throws InvalidInputException if room number is invalid
     */
    public static void validateRoom(int room) {
        if (room < MIN_ROOM_NUMBER || room > MAX_ROOM_NUMBER) {
            throw new InvalidInputException("Room number must be between " + 
                MIN_ROOM_NUMBER + " and " + MAX_ROOM_NUMBER);
        }
    }
    
    /**
     * Validates item count
     * @param count the count to validate
     * @throws InvalidInputException if count is negative
     */
    public static void validateCount(int count) {
        if (count <= 0) {
            throw new InvalidInputException("Count must be positive");
        }
    }
    
    /**
     * Validates that the specified object is not null
     * @param object the object to check
     * @param name the name to use in the exception message
     * @throws InvalidInputException if object is null
     */
    public static void validateNotNull(Object object, String name) {
        if (object == null) {
            throw new InvalidInputException(name + " cannot be null");
        }
    }
    
    /**
     * Validates that the specified string is not null or empty
     * @param string the string to check
     * @param name the name to use in the exception message
     * @throws InvalidInputException if string is null or empty
     */
    public static void validateNotEmpty(String string, String name) {
        if (string == null || string.trim().isEmpty()) {
            throw new InvalidInputException(name + " cannot be null or empty");
        }
    }
    
    /**
     * Validates that the specified UUID is not null
     * @param id the UUID to check
     * @throws InvalidInputException if UUID is null
     */
    public static void validateOrderId(UUID id) {
        if (id == null) {
            throw new InvalidInputException("Order ID cannot be null");
        }
    }
}