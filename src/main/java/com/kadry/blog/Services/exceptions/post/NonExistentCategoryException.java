package com.kadry.blog.Services.exceptions.post;

public class NonExistentCategoryException extends RuntimeException{

    public NonExistentCategoryException(String message) {
        super(message);
    }
}
