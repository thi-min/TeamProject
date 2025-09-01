package com.project.fund.exception;

public class FundException extends RuntimeException {
    public FundException() { super(); }
    public FundException(String message) { super(message); }
    public FundException(String message, Throwable cause) { super(message, cause); }
}