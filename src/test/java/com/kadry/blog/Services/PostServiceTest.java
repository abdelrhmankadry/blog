package com.kadry.blog.Services;

import com.kadry.blog.Services.Imp.PostServiceImpl;
import com.kadry.blog.dto.post.CreatePostResponse;
import com.kadry.blog.dto.post.PostDto;
import com.kadry.blog.dto.post.UpdatePostDto;
import com.kadry.blog.mapper.PostDtoToPost;
import com.kadry.blog.model.Category;
import com.kadry.blog.model.Post;
import com.kadry.blog.model.User;
import com.kadry.blog.object_mother.PostDtoObjectMother;
import com.kadry.blog.object_mother.UserObjectMother;
import com.kadry.blog.repositories.CategoryRepository;
import com.kadry.blog.repositories.PostRepository;
import com.kadry.blog.repositories.UserRepository;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class PostServiceTest {

    PostService postService;
    PostDtoObjectMother postDtoObjectMother;

    @Mock
    PostRepository postRepository;

    @Captor
    ArgumentCaptor<Post> postCaptor;

    PostDtoToPost postDtoToPost;

    @Mock
    UserRepository userRepository;

    @Mock
    CategoryRepository categoryRepository;

    @Before
    public void setUp() throws Exception {
        postDtoToPost = new PostDtoToPost(categoryRepository, userRepository);
        postService = new PostServiceImpl(postRepository, userRepository, postDtoToPost, categoryRepository);
        postDtoObjectMother = new PostDtoObjectMother();
    }

    @Test
    public void testCreatePost() {
        when(userRepository.findUserByUsername(any())).thenReturn(Optional.of(new User()));
        when(categoryRepository.findCategoryByName(any())).thenReturn(Optional.of(new Category()));

        PostDto postDto = postDtoObjectMother.createDefaultPostDto().get();

        CreatePostResponse createPostResponse = postService.createPost(postDto, UserObjectMother.TEST_USERNAME);

        verify(postRepository).save(postCaptor.capture());
        assertNotNull(createPostResponse.getCreatedPostId());

        Post post = postCaptor.getValue();
        assertEquals(PostDtoObjectMother.TEST_TITLE, post.getTitle());
        assertNotNull(post.getUser());
        assertNotNull(post.getCategory());
    }

    @Test
    public void testUpdatePost() {
        when(postRepository.findPostByUuid(anyString())).thenReturn(Optional.of(new Post()));
        Category category = createCategory();
        when(categoryRepository.findCategoryByName(anyString())).thenReturn(Optional.of(category));
        UpdatePostDto updatePostDto = createUpdatePostDto();

        postService.updatePost(updatePostDto, UUID.randomUUID().toString());

        verify(postRepository).save(postCaptor.capture());
        Post savedPost = postCaptor.getValue();
        assertEquals(updatePostDto.getBody(), savedPost.getBody());
        assertEquals(updatePostDto.getCategory(), savedPost.getCategory().getName());
    }

    @Test
    public void testDeletePost() {
        when(postRepository.findPostByUuid(any())).thenReturn(Optional.of(new Post()));
        postService.deletePost("test-uuid");
        verify(postRepository).delete(any());
    }

    private Category createCategory() {
        Category category = new Category();
        category.setName("test-new-category");
        return category;
    }

    private UpdatePostDto createUpdatePostDto() {
        UpdatePostDto updatePostDto = new UpdatePostDto();
        updatePostDto.setBody("test-new-body");
        updatePostDto.setCategory("test-new-category");
        return updatePostDto;
    }
}