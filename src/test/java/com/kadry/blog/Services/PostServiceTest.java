package com.kadry.blog.Services;

import com.kadry.blog.Services.Imp.PostServiceImpl;
import com.kadry.blog.dto.post.CreatePostResponse;
import com.kadry.blog.dto.post.PostDto;
import com.kadry.blog.mapper.PostDtoToPost;
import com.kadry.blog.model.Category;
import com.kadry.blog.model.Post;
import com.kadry.blog.model.User;
import com.kadry.blog.object_mother.PostObjectMother;
import com.kadry.blog.object_mother.UserObjectMother;
import com.kadry.blog.repositories.CategoryRepository;
import com.kadry.blog.repositories.PostRepository;
import com.kadry.blog.repositories.UserRepository;
import com.kadry.blog.security.SecurityUtils;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class PostServiceTest {

    PostService postService;
    PostObjectMother postObjectMother;

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
        postService = new PostServiceImpl(postRepository, userRepository, postDtoToPost);
        postObjectMother = new PostObjectMother();
    }

    @Test
    public void testCreatePost() {
        when(userRepository.findUserByUsername(any())).thenReturn(Optional.of(new User()));
        when(categoryRepository.findCategoryByName(any())).thenReturn(Optional.of(new Category()));

        PostDto postDto = postObjectMother.createDefaultPostDto();

        CreatePostResponse createPostResponse = postService.createPost(postDto, UserObjectMother.TEST_USERNAME);

        verify(postRepository).save(postCaptor.capture());
        assertNotNull(createPostResponse.getCreatedPostId());

        Post post = postCaptor.getValue();
        assertEquals(PostObjectMother.TEST_TITLE, post.getTitle());
        assertNotNull(post.getUser());
        assertNotNull(post.getCategory());
    }
}