package com.kadry.blog.dto;


import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;

@Data
public class UpdateUserDto {

    private String firstName;
    private String lastName;

    @JsonProperty("favorite_categories")
    private List<String> favoriteCategories;
}
