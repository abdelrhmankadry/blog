package com.kadry.blog.Services.exceptions;

public class InvalidResetKeyException extends RuntimeException {
    public InvalidResetKeyException() {
        super("Invalid Reset password Key!");
    }
}
