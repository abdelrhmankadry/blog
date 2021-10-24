package com.kadry.blog.dto.post;

import lombok.Data;

import javax.validation.constraints.NotEmpty;

@Data
public class UpdatePostDto {

    @NotEmpty
    private String body;
    private String category;
}
