package org.example.services;

public class InitialisationException extends Exception {
    public InitialisationException(String message) {
        super(message);
    }

    public InitialisationException(String message, Throwable cause) {
        super(message, cause);
    }
}