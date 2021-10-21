package com.kadry.blog.Services;

import com.kadry.blog.dto.post.CreatePostResponse;
import com.kadry.blog.dto.post.PostDto;

public interface PostService {

    CreatePostResponse createPost(PostDto postDto, String username);
}
