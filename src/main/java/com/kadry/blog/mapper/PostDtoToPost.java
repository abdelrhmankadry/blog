package com.kadry.blog.mapper;


import com.kadry.blog.Services.exceptions.UnAuthenticatedAccessException;
import com.kadry.blog.dto.post.PostDto;
import com.kadry.blog.model.Post;
import com.kadry.blog.repositories.CategoryRepository;
import com.kadry.blog.repositories.UserRepository;
import com.kadry.blog.security.SecurityUtils;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class PostDtoToPost implements Converter<PostDto, Post> {

    private final CategoryRepository categoryRepository;
    private final UserRepository userRepository;

    public PostDtoToPost(CategoryRepository categoryRepository, UserRepository userRepository) {
        this.categoryRepository = categoryRepository;
        this.userRepository = userRepository;
    }

    @Override
    public Post convert(PostDto source) {
        if (source == null){
            return null;
        }
        final Post post = new Post();

        post.setTitle(source.getTitle());
        post.setBody(source.getBody());
        categoryRepository.findCategoryByName(source.getCategory())
                .ifPresent(post::setCategory);
        post.setPostDate(source.getDate());
        post.setUuid(UUID.randomUUID().toString());

        return post;
    }
}
