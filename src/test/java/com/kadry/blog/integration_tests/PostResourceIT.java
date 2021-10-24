package com.kadry.blog.integration_tests;

import com.kadry.blog.dto.post.PostDto;
import com.kadry.blog.dto.post.UpdatePostDto;
import com.kadry.blog.model.Category;
import com.kadry.blog.model.Post;
import com.kadry.blog.object_mother.PostObjectMother;
import com.kadry.blog.object_mother.UserObjectMother;
import com.kadry.blog.repositories.CategoryRepository;
import com.kadry.blog.repositories.PostRepository;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Date;

import static com.kadry.blog.TestUtils.asJsonString;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
public class PostResourceIT {

    private static final String TEST_UPDATE_CATEGORY = "test-update-category";
    @Autowired
    MockMvc mockMvc;

    @Autowired
    private PostRepository postRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    private PostObjectMother postObjectMother;

    @Before
    public void setUp() throws Exception {
        postObjectMother = new PostObjectMother();

    }

    @Test
    @WithMockUser(UserObjectMother.TEST_USERNAME)
    public void testCreatePost() throws Exception {
        PostDto postDto = new PostDto();
        postDto.setTitle("test-title");
        postDto.setCategory("test-category");
        postDto.setDate((new Date()));
        postDto.setBody("test-body");

        mockMvc.perform(post("/api/post/create")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(asJsonString(postDto)))
                            .andExpect(status().isOk())
                            .andExpect(jsonPath("$.created_post_id").exists());

        assertTrue(postRepository.findPostByTitle("test-title").isPresent());

    }

    @Test
    @WithMockUser(UserObjectMother.TEST_USERNAME)
    public void testUpdatePost() throws Exception {

        persistTestCategory();
        Post post = persistTestPost();

        UpdatePostDto updatePostDto = new UpdatePostDto();
        updatePostDto.setBody("test-new-body");
        updatePostDto.setCategory("test-new-category");

        mockMvc.perform(post("/api/post/update")
                            .param("post_id", post.getUuid())
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(asJsonString(updatePostDto)))
                .andExpect(status().isOk());

        Post returnedPost = postRepository.findEagerlyPostByUuid(post.getUuid()).get();

        assertEquals(updatePostDto.getBody(), returnedPost.getBody());
        assertEquals(updatePostDto.getCategory(), returnedPost.getCategory().getName());
    }


    private Post persistTestPost() {
        Post post = postObjectMother.createDefaultPost();
        post.setCategory(categoryRepository.findCategoryByName(TEST_UPDATE_CATEGORY).get());
        postRepository.save(post);
        return post;
    }

    private void persistTestCategory() {
        Category category = new Category();
        category.setName(TEST_UPDATE_CATEGORY);
        categoryRepository.save(category);
    }
}
