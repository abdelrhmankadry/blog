package com.kadry.blog.integration_tests;

import com.kadry.blog.Services.exceptions.UnActivatedUserException;
import com.kadry.blog.model.User;
import com.kadry.blog.payload.LoginVM;
import com.kadry.blog.repositories.UserRepository;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import static com.kadry.blog.TestUtils.asJsonString;
import static org.hamcrest.Matchers.*;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@RunWith(SpringRunner.class)
@AutoConfigureMockMvc
@SpringBootTest
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
public class UserJwtControllerTestIT {

    @Autowired
    UserRepository userRepository;

    @Autowired
    PasswordEncoder passwordEncoder;

    @Autowired
    MockMvc mockMvc;

    @Before
    public void setUp() throws Exception {
        User user1 = new User();
        user1.setUsername("test-username");
        user1.setPassword(passwordEncoder.encode("test-password"));
        user1.setEmail("test@domain.com");
        user1.setActivated(true);

        userRepository.save(user1);

        User user2 = new User();
        user2.setUsername("test-username2");
        user2.setPassword(passwordEncoder.encode("test-password2"));
        user2.setEmail("test2@domain.com");
        user2.setActivated(false);

        userRepository.save(user2);
    }

    @Test
    public void testAuthenticate() throws Exception {

        LoginVM loginVM = new LoginVM();
        loginVM.setUsername("test-username");
        loginVM.setPassword("test-password");

        mockMvc.perform(post("/api/authenticate")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(asJsonString(loginVM)))
                        .andExpect(jsonPath("$.token").isString())
                        .andExpect(jsonPath("$.token").isNotEmpty())
                        .andExpect(header().string("Authorization",not(nullValue())))
                        .andExpect(header().string("Authorization", not(is(emptyString()))));

    }

    @Test
    public void testAuthenticateWithInvalidCredential() throws Exception {
        LoginVM loginVM = new LoginVM();
        loginVM.setUsername("test-username");
        loginVM.setPassword("wrong-password");

        mockMvc.perform(post("/api/authenticate")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(asJsonString(loginVM)))
                         .andExpect(status().isUnauthorized());;

    }

    @Test
    public void testAuthenticateWithNonActivatedUser() throws Exception {
        LoginVM loginVM = new LoginVM();
        loginVM.setUsername("test-username2");
        loginVM.setPassword("test-password2");

        mockMvc.perform(post("/api/authenticate")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(asJsonString(loginVM)))
                        .andExpect(status().isUnauthorized());
    }
}