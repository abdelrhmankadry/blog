package com.kadry.blog.Services.Imp;

import com.kadry.blog.Services.PostService;
import com.kadry.blog.Services.exceptions.UnAuthenticatedAccessException;
import com.kadry.blog.dto.post.CreatePostResponse;
import com.kadry.blog.dto.post.PostDto;
import com.kadry.blog.mapper.PostDtoToPost;
import com.kadry.blog.model.Post;
import com.kadry.blog.repositories.PostRepository;
import com.kadry.blog.repositories.UserRepository;
import com.kadry.blog.security.SecurityUtils;
import org.springframework.stereotype.Service;

@Service
public class PostServiceImpl implements PostService {

    private final PostRepository postRepository;
    private final PostDtoToPost postDtoToPost;
    private final UserRepository userRepository;

    public PostServiceImpl(PostRepository postRepository, UserRepository userRepository, PostDtoToPost postDtoToPost) {
        this.postRepository = postRepository;
        this.postDtoToPost = postDtoToPost;
        this.userRepository = userRepository;
    }

    @Override
    public CreatePostResponse createPost(PostDto postDto, String username) {
        Post post = postDtoToPost.convert(postDto);


        userRepository.findUserByUsername(username)
                .ifPresent(post::setUser);

        postRepository.save(post);

        CreatePostResponse createPostResponse = new CreatePostResponse();
        createPostResponse.setCreatedPostId(post.getUuid());
        return createPostResponse;
    }

}
