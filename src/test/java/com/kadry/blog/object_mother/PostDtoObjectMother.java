package com.kadry.blog.object_mother;

import com.kadry.blog.dto.post.PostDto;

import java.util.Date;


public class PostDtoObjectMother {

    public static final String TEST_TITLE = "test-title";
    public static final String TEST_BODY = "test-body";
    public static final String TEST_CATEGORY = "test-category";
    public static final Date TEST_DATE = new Date();

    private PostDto postDto;

    public PostDtoObjectMother createDefaultPostDto(){
        postDto = new PostDto();
        postDto.setTitle(TEST_TITLE);
        postDto.setBody(TEST_BODY);
        postDto.setDate(TEST_DATE);
        postDto.setCategory(TEST_CATEGORY);
        return this;
    }

    public PostDto get(){
        return postDto;
    }
}
