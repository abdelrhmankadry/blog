package com.kadry.blog.web.rest;

import com.kadry.blog.Services.PostService;
import com.kadry.blog.dto.post.CreatePostResponse;
import com.kadry.blog.dto.post.PostDto;
import com.kadry.blog.dto.post.UpdatePostDto;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

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

    @PostMapping("/update")
    public void updatePost(@RequestBody UpdatePostDto updatePostDto, @RequestParam(name = "post_id") String uuid){
        postService.updatePost(updatePostDto, uuid);
    }
}
