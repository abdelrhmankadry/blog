package com.kadry.blog.Services.Imp;

import com.kadry.blog.Services.PostService;
import com.kadry.blog.Services.exceptions.post.NonExistentCategoryException;
import com.kadry.blog.Services.exceptions.post.PostNotFoundException;
import com.kadry.blog.dto.post.CreatePostResponse;
import com.kadry.blog.dto.post.PostDto;
import com.kadry.blog.dto.post.UpdatePostDto;
import com.kadry.blog.mapper.PostDtoToPost;
import com.kadry.blog.model.Category;
import com.kadry.blog.model.Post;
import com.kadry.blog.repositories.CategoryRepository;
import com.kadry.blog.repositories.PostRepository;
import com.kadry.blog.repositories.UserRepository;
import org.springframework.stereotype.Service;

@Service
public class PostServiceImpl implements PostService {

    private final PostRepository postRepository;
    private final PostDtoToPost postDtoToPost;
    private final UserRepository userRepository;
    private final CategoryRepository categoryRepository;

    public PostServiceImpl(PostRepository postRepository, UserRepository userRepository, PostDtoToPost postDtoToPost, CategoryRepository categoryRepository) {
        this.postRepository = postRepository;
        this.postDtoToPost = postDtoToPost;
        this.userRepository = userRepository;
        this.categoryRepository = categoryRepository;
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

    @Override
    public void updatePost(UpdatePostDto updatePostDto, String uuid) {

        Post post = postRepository.findPostByUuid(uuid)
                .orElseThrow(PostNotFoundException::new);

        String newBody = updatePostDto.getBody();
        if(newBody != null){
            post.setBody(newBody);
        }

        String newCategory = updatePostDto.getCategory();
        if(newCategory != null){
            Category category = categoryRepository.findCategoryByName(newCategory)
                    .orElseThrow(() -> new NonExistentCategoryException("Category " + newCategory + " does not exist!!"));
            post.setCategory(category);
        }

        postRepository.save(post);
    }

    @Override
    public void deletePost(String uuid) {
        Post post = postRepository.findPostByUuid(uuid)
                .orElseThrow(PostNotFoundException::new);
        postRepository.delete(post);
    }

}
