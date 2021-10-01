package com.kadry.blog.dto;

import com.kadry.blog.config.Constants;
import lombok.Builder;
import lombok.Data;
import lombok.RequiredArgsConstructor;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

@Data
public class UserDto {

    @NotBlank
    @Pattern(regexp = Constants.LOGIN_REGEX)
    @Size(min = 1, max = 50)
    String username;

    @Size(min = 8,max = 50)
    String password;

    @Email
    @Size(min = 6, max = 254)
    String email;

    @Size(max = 50)
    String firstName;
    @Size(max = 50)
    String lastName;

    public UserDto(@NotBlank @Pattern(regexp = Constants.LOGIN_REGEX) @Size(min = 1, max = 50) String username,
                   @Size(min = 8, max = 50) String password,
                   @Email @Size(min = 6, max = 254) String email,
                   @Size(max = 50) String firstName,
                   @Size(max = 50) String lastName) {
        this.username = username;
        this.password = password;
        this.email = email;
        this.firstName = firstName;
        this.lastName = lastName;
    }

    public UserDto() {
    }
}
