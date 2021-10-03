package com.kadry.blog.Services.Imp;

public class InvalidResetKey extends RuntimeException {
    public InvalidResetKey() {
        super("Invalid Reset password Key!");
    }
}
