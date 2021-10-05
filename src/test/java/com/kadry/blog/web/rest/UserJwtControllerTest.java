package com.kadry.blog.web.rest;

import com.kadry.blog.payload.LoginVM;
import com.kadry.blog.security.jwt.TokenProvider;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static com.kadry.blog.TestUtils.asJsonString;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@WebMvcTest(UserJwtController.class)
public class UserJwtControllerTest {

    private static final String TEST_JWT = "test-jwt";

    @Autowired
    MockMvc mockMvc;

    @MockBean
    TokenProvider tokenProvider;


    @MockBean
    AuthenticationManager authenticationManager;

    @Test
    public void testAuthenticate() throws Exception {
        when(tokenProvider.generateToken(any(Authentication.class))).thenReturn(TEST_JWT);
        when(authenticationManager.authenticate(any(Authentication.class)))
                .thenReturn(new UsernamePasswordAuthenticationToken("test-principle",
                        null, List.of(()->"test-authority")));

        LoginVM loginVM = new LoginVM();
        loginVM.setUsername("test-username");
        loginVM.setPassword("test-password");

        mockMvc.perform(post("/api/authenticate")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(asJsonString(loginVM)))
                            .andExpect(status().isOk())
                            .andExpect(jsonPath("$.token").value(TEST_JWT));
    }
}