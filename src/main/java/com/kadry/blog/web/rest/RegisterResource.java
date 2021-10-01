package com.kadry.blog.web.rest;

import com.kadry.blog.Services.MailService;
import com.kadry.blog.Services.RegisterService;
import com.kadry.blog.dto.UserDto;
import com.kadry.blog.model.User;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequestMapping("/api")
public class RegisterResource {

    private final RegisterService registerService;
    private final MailService mailService;
    public RegisterResource(RegisterService registerService, MailService mailService) {
        this.registerService = registerService;
        this.mailService = mailService;
    }

    @PostMapping("/register")
    @ResponseStatus(HttpStatus.CREATED)
    public void register(@Valid @RequestBody UserDto userDto){
        User user = registerService.registerNewUser(userDto);
        mailService.sendActivationMail(user);
    }

}
