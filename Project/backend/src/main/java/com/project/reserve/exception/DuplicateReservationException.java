package com.project.reserve.exception;

public class DuplicateReservationException extends RuntimeException {
	
    public DuplicateReservationException(String message) {
        super(message);
    }
}