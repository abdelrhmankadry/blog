package com.kadry.blog.Services.exceptions.user;

public class InvalidResetKeyException extends RuntimeException {
    public InvalidResetKeyException() {
        super("Invalid Reset password Key!");
    }
}
