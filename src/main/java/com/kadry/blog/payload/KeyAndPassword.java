package com.kadry.blog.payload;

import lombok.Data;

import javax.validation.constraints.Size;

@Data
public class KeyAndPassword {

    @Size(max = 20)
    private String key;
    @Size(min = 8,max = 50)
    private String password;
}
