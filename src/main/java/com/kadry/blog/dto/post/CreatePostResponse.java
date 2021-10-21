package com.kadry.blog.dto.post;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class CreatePostResponse {

    @JsonProperty("created_post_id")
    String createdPostId;
}
