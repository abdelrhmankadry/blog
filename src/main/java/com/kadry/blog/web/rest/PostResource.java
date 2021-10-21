package com.kadry.blog.web.rest;

import com.kadry.blog.Services.PostService;
import com.kadry.blog.dto.post.CreatePostResponse;
import com.kadry.blog.dto.post.PostDto;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/post")
public class PostResource {

    private final PostService postService;

    public PostResource(PostService postService) {
        this.postService = postService;
    }

    @PostMapping("/create")
    public CreatePostResponse createPost(@RequestBody PostDto postDto, Authentication authentication){
        return postService.createPost(postDto, authentication.getName());
    }
}
