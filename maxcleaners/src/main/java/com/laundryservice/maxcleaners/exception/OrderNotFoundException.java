package com.laundryservice.maxcleaners.exception;

/**
 * Author Tejesh
 */
public class OrderNotFoundException extends RuntimeException {
    public OrderNotFoundException(String message) {
        super(message);
    }
}