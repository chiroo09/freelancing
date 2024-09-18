package com.laundryservice.maxcleaners.exception;

/**
 * Author Tejesh
 */
public class OrderAlreadyUpdatedException extends RuntimeException {
    public OrderAlreadyUpdatedException(String message) {
        super(message);
    }
}
