package com.kadry.blog.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import javax.validation.constraints.Size;

@Data
public class PasswordChangedDto {

    @JsonProperty("current_password")
    private String currentPassword;

    @Size(min = 8,max = 50)
    @JsonProperty("new_password")
    private String newPassword;
}
