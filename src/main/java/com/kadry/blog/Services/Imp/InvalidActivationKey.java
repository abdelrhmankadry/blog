package com.kadry.blog.Services.Imp;

public class InvalidActivationKey extends RuntimeException {
    public InvalidActivationKey() {
        super("Activation key is invalid!!");
    }
}
