package com.Library.Management.exception;

public class InvalidStatusTransitionException extends BusinessException {
    public InvalidStatusTransitionException(String message) {
        super(message);
    }
}
