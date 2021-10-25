package com.kadry.blog.Services;

import com.kadry.blog.dto.post.CreatePostResponse;
import com.kadry.blog.dto.post.PostDto;
import com.kadry.blog.dto.post.UpdatePostDto;

public interface PostService {

    CreatePostResponse createPost(PostDto postDto, String username);

    void updatePost(UpdatePostDto updatePostDto, String uuid);

    void deletePost(String uuid);
}
