package com.kadry.blog.Services.exceptions.user;

import org.springframework.security.core.AuthenticationException;

public class UnAuthenticatedAccessException extends AuthenticationException {
    public UnAuthenticatedAccessException() {
        super("Unauthenticated access!");
    }
}
