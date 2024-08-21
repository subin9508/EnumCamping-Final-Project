package com.itwill.finalproject.exception;

import org.springframework.security.core.AuthenticationException;

public class UserAccountDeactivatedException extends AuthenticationException {

    public UserAccountDeactivatedException(String message) {
        super(message);
    }
}