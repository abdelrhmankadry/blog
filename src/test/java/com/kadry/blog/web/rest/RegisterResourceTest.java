package com.kadry.blog.web.rest;

import com.kadry.blog.Services.MailService;
import com.kadry.blog.Services.RegisterService;
import com.kadry.blog.dto.UserDto;
import com.kadry.blog.model.User;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import static com.kadry.blog.TestUtils.asJsonString;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@RunWith(SpringRunner.class)
@WebMvcTest(RegisterResource.class)
class RegisterResourceTest {

    @Autowired
    MockMvc mockMvc;

    @MockBean
    RegisterService registerService;

    @MockBean
    MailService mailService;

    @Test
    void testRegisterNewUser() throws Exception {
        UserDto userDto = new UserDto("test_username",
                "test_password",
                "test@testdomain.com",
                "test-firstname",
                "test-lastname");



        mockMvc.perform(post("/api/register", userDto)
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(userDto)))
                .andExpect(status().isCreated());

        verify(registerService).registerNewUser(any(UserDto.class));
        verify(mailService).sendActivationMail(any(User.class));

    }


    @Test
    void testRegisterNewUserWithInvalidUsername() throws Exception {
        UserDto userDto = new UserDto("test  username",
                "test_password",
                "test@testdomain.com",
                "test-firstname",
                "test-lastname");

        mockMvc.perform(post("/api/register", userDto)
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(userDto)))
                .andExpect(status().isBadRequest());
    }


}