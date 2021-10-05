package com.kadry.blog.Services.exceptions;

import org.springframework.security.core.AuthenticationException;

public class UnActivatedUserException extends AuthenticationException {
    private static final long serialVersionUID = 1L;
    public UnActivatedUserException() {
        super("User account is not activated yey!");
    }

    public UnActivatedUserException(String msg, Throwable cause) {
        super(msg, cause);
    }

    public UnActivatedUserException(String msg) {
        super(msg);
    }
}
