package com.smartgym.exception;

/**
 * Custom Exception for user login and authentication failures.
 * Demonstrates:
 * - Inheritance (extends GymException)
 * - Constructor Overloading
 */
public class AuthenticationException extends GymException {

    public AuthenticationException() {
        super("Invalid username or password. Access denied.");
    }

    public AuthenticationException(String message) {
        super(message);
    }

    public AuthenticationException(String message, Throwable cause) {
        super(message, cause);
    }
}

