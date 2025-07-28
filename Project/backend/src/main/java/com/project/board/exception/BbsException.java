package com.project.board.exception;

public class BbsException extends RuntimeException {
	public BbsException(String message) {
		super(message);
	}

	public BbsException(String message, Throwable cause) {
        super(message, cause);
    }
}