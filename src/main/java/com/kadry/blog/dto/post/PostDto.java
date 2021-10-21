package com.kadry.blog.dto.post;

import com.kadry.blog.model.Category;
import lombok.Data;

import javax.validation.constraints.NotEmpty;
import java.util.Date;
import java.util.List;

@Data
public class PostDto {

    @NotEmpty
    private String title;

    @NotEmpty
    private String body;

    private String category;

    private Date date;

}
