package com.kadry.blog.payload;

import lombok.Data;

@Data
public class JwtToken {
    String token;

    public JwtToken(String token) {
        this.token = token;
    }
}
