package com.kadry.blog.web.rest;

import com.kadry.blog.Services.MailService;
import com.kadry.blog.Services.UserService;
import com.kadry.blog.dto.UserDto;
import com.kadry.blog.model.User;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequestMapping("/api")
public class AccountResource {

    private final UserService userService;
    private final MailService mailService;
    public AccountResource(UserService userService, MailService mailService) {
        this.userService = userService;
        this.mailService = mailService;
    }

    @PostMapping("/register")
    @ResponseStatus(HttpStatus.CREATED)
    public void register(@Valid @RequestBody UserDto userDto){
        User user = userService.registerNewUser(userDto);
        mailService.sendActivationMail(user);
    }

}
