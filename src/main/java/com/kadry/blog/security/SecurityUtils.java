package com.kadry.blog.security;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.Optional;

public class SecurityUtils {

    static public Optional<String> getCurrentUserLogin(){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if(authentication == null){
            return Optional.empty();
        }else {
            return Optional.of(authentication.getName());
        }
    }

}
