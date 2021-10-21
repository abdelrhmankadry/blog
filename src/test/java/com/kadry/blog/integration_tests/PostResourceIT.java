package com.kadry.blog.integration_tests;

import com.kadry.blog.dto.post.PostDto;
import com.kadry.blog.object_mother.UserObjectMother;
import com.kadry.blog.repositories.PostRepository;
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
import static org.junit.Assert.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
public class PostResourceIT {

    @Autowired
    MockMvc mockMvc;

    @Autowired
    private PostRepository postRepository;

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
}
