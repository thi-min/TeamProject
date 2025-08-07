package com.project.common.exception;

//중복 예외 (아이디, 휴대폰번호 등)
public class DuplicateException extends RuntimeException {
    public DuplicateException(String message) {
        super(message);
    }
}
