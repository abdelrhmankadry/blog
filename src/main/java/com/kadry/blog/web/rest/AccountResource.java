package com.kadry.blog.web.rest;

import com.kadry.blog.Services.MailService;
import com.kadry.blog.Services.UserService;
import com.kadry.blog.dto.PasswordChangedDto;
import com.kadry.blog.dto.UserDto;
import com.kadry.blog.model.User;
import com.kadry.blog.payload.KeyAndPassword;
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

    @GetMapping("/activate")
    public void activateAccount(@RequestParam(value = "key") String key){
        userService.activateUser(key);
    }

    @PostMapping("/account/reset-password/init")
    public void resetPasswordInit(@RequestBody String email){
        userService.resetPasswordInit(email).ifPresent(mailService::sendResetPasswordMail);
    }

    @PostMapping("/account/reset-password/final")
    public void resetPasswordFinal(@Valid @RequestBody KeyAndPassword keyAndPassword){
        userService.resetPasswordFinal(keyAndPassword);
    }

    @PostMapping("/account/change-password")
    public void changePassword(@Valid @RequestBody PasswordChangedDto passwordChangedDto){
        userService.changePassword(passwordChangedDto);
    }

    @DeleteMapping("account/delete")
    public void deleteUserAccount(){
        userService.deleteUserAccount();;
    }
}
