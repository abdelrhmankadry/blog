package com.kadry.blog.Services.exceptions;

public class InvalidActivationKeyException extends RuntimeException {
    public InvalidActivationKeyException() {
        super("Activation key is invalid!!");
    }
}
