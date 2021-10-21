package com.kadry.blog.web.rest;

import com.kadry.blog.Services.MailService;
import com.kadry.blog.Services.UserService;
import com.kadry.blog.dto.user.PasswordChangedDto;
import com.kadry.blog.dto.user.UserDto;
import com.kadry.blog.model.User;
import com.kadry.blog.payload.KeyAndPassword;
import org.junit.Test;
import org.junit.runner.RunWith;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Optional;

import static com.kadry.blog.TestUtils.asJsonString;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@RunWith(SpringRunner.class)
@WebMvcTest(AccountResource.class)
@ComponentScan({"com.kadry.blog.security.jwt"})
public class AccountResourceTest {

    @Autowired
    MockMvc mockMvc;

    @MockBean
    UserService userService;

    @MockBean
    MailService mailService;


    @Test
    public void testRegisterNewUser() throws Exception {

        when(userService.registerNewUser(any(UserDto.class))).thenReturn(new User());

        UserDto userDto = new UserDto("test_username",
                "test_password",
                "test@testdomain.com",
                "test-firstname",
                "test-lastname");



        mockMvc.perform(post("/api/register", userDto)
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(userDto)))
                .andExpect(status().isCreated());

        verify(userService).registerNewUser(any(UserDto.class));
        verify(mailService).sendActivationMail(any(User.class));

    }


    @Test
    public void testRegisterNewUserWithInvalidUsername() throws Exception {
        UserDto userDto = new UserDto("test  username",
                "test_password",
                "test@testdomain.com",
                "test-firstname",
                "test-lastname");

        mockMvc.perform(post("/api/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(userDto)))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void testActivateAccount() throws Exception {
        String activationKey = "test activation key";

        mockMvc.perform(get("/api/activate?key={activationKey}", activationKey))
                .andExpect(status().isOk());

        verify(userService).activateUser(activationKey);
    }

    @Test
    public void testResetPasswordInit() throws Exception {
        when(userService.resetPasswordInit(anyString())).thenReturn(Optional.of(new User()));

        mockMvc.perform(post("/api/account/reset-password/init")
                .contentType(MediaType.APPLICATION_JSON)
                .content("test@domain.com"))
                .andExpect(status().isOk());

        verify(userService).resetPasswordInit(anyString());
        verify(mailService).sendResetPasswordMail(any(User.class));
    }

    @Test
    public void testResetPasswordFinal() throws Exception {

        KeyAndPassword keyAndPassword = new KeyAndPassword();
        keyAndPassword.setPassword("test-password");
        keyAndPassword.setKey("test-key");

        mockMvc.perform(post("/api/account/reset-password/final")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(asJsonString(keyAndPassword)))
                .andExpect(status().isOk());

        verify(userService).resetPasswordFinal(any(KeyAndPassword.class));
    }

    @Test
    @WithMockUser("test-current-password")
    public void testChangePassword() throws Exception {
        PasswordChangedDto passwordChangedDto = new PasswordChangedDto();
        passwordChangedDto.setCurrentPassword("test-current-password");
        passwordChangedDto.setNewPassword("test-new-password");
        mockMvc.perform(post("/api/account/change-password")
                                    .contentType(MediaType.APPLICATION_JSON)
                                    .content(asJsonString(passwordChangedDto)))
                                    .andExpect(status().isOk());

    }

    @Test
    public void testChangePasswordWithoutAuthentication() throws Exception {
        mockMvc.perform(post("/api/account/change-password")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(asJsonString(new PasswordChangedDto())))
                            .andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser("test-user")
    public void testDeleteUserAccount() throws Exception {
        mockMvc.perform(delete("/api/account/delete"))
                .andExpect(status().isOk());

        verify(userService).deleteUserAccount();
    }
}