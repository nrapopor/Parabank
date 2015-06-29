package com.parasoft.parabank.service;

/**
 * Default class for service errors
 */
@SuppressWarnings("serial")
public class ParaBankServiceException extends Exception {
    public ParaBankServiceException(String message) {
        super(message);
    }
}
