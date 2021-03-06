package com.kadry.blog.integration_tests;

import com.kadry.blog.dto.user.PasswordChangedDto;
import com.kadry.blog.dto.user.UpdateUserDto;
import com.kadry.blog.dto.user.UserDto;
import com.kadry.blog.model.User;
import com.kadry.blog.object_mother.UserObjectMother;
import com.kadry.blog.payload.KeyAndPassword;
import com.kadry.blog.repositories.UserRepository;
import com.kadry.blog.security.AuthoritiesConstants;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static com.kadry.blog.TestUtils.asJsonString;
import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
public class AccountResourceIT {

    public static final String NEW_TEST_PASSWORD = "new_test_password";
    private static final String TEST_USERNAME = "test_username";
    private static final String TEST_ACTIVATION_KEY = "test_activation_key";
    private static final String TEST_PASSWORD = "test_password";
    private static final String TEST_EMAIL = "test_email@testdomain.com";
    @Autowired
    UserRepository userRepository;

    TestRestTemplate testRestTemplate;

    @Autowired
    MockMvc mockMvc;

    UserObjectMother userObjectMother;
    @Before
    public void setUp() throws Exception {
        testRestTemplate = new TestRestTemplate();
        userObjectMother = new UserObjectMother();
    }

    @Test
    public void testRegisterNewUser() throws Exception {
        UserDto userDTO = new UserDto();
        userDTO.setUsername(TEST_USERNAME);
        userDTO.setPassword(TEST_PASSWORD);
        userDTO.setEmail(TEST_EMAIL);
        userDTO.setFirstName("test_firstname");
        userDTO.setLastName("test_lastname");


        mockMvc.perform(post("/api/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(userDTO)))
                .andExpect(status().isCreated());

        assertTrue(userRepository.findUserByUsername(TEST_USERNAME).isPresent());
    }

    @Test
    public void testActivateAccount() throws Exception {
        User user = new User();
        user.setUsername(TEST_USERNAME);
        user.setPassword(TEST_PASSWORD);
        user.setActivationKey(TEST_ACTIVATION_KEY);
        user.setActivated(false);

        userRepository.save(user);

        mockMvc.perform(get("/api/activate?key={activationKey}", TEST_ACTIVATION_KEY))
                .andExpect(status().isOk());

        User returnedUser = userRepository.findUserByUsername(TEST_USERNAME).get();
        assertTrue(returnedUser.isActivated());
    }

    @Test
    public void testResetPassword() throws Exception {
        User user = new User();
        user.setUsername(TEST_USERNAME);
        user.setPassword(TEST_PASSWORD);
        user.setEmail(TEST_EMAIL);
        user.setActivated(true);

        userRepository.save(user);

        mockMvc.perform(post("/api/account/reset-password/init")
                .content(TEST_EMAIL))
                .andExpect(status().isOk());

        User returnedUser = userRepository.findUserByEmail(TEST_EMAIL).get();
        assertNotNull(returnedUser.getResetKey());
        assertNotNull(returnedUser.getResetDate());

        KeyAndPassword keyAndPassword = new KeyAndPassword();
        keyAndPassword.setKey(returnedUser.getResetKey());
        keyAndPassword.setPassword(NEW_TEST_PASSWORD);

        mockMvc.perform(post("/api/account/reset-password/final")
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(keyAndPassword)))
                .andExpect(status().isOk());


        returnedUser = userRepository.findUserByEmail(TEST_EMAIL).get();
        assertEquals(NEW_TEST_PASSWORD, returnedUser.getPassword());
    }

    @Test
    @WithMockUser(username = TEST_USERNAME, password = TEST_PASSWORD, authorities = AuthoritiesConstants.USER)
    public void testChangePassword() throws Exception {

        PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        final String TEST_NEW_PASSWORD = "test-new_password";

        User user = new User();
        user.setUsername(TEST_USERNAME);
        user.setPassword(passwordEncoder.encode(TEST_PASSWORD));
        userRepository.save(user);

        PasswordChangedDto passwordChangedDto = new PasswordChangedDto();
        passwordChangedDto.setCurrentPassword(TEST_PASSWORD);
        passwordChangedDto.setNewPassword(TEST_NEW_PASSWORD);


        mockMvc.perform(post("/api/account/change-password")
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(passwordChangedDto)))
                .andExpect(status().isOk());

        assertTrue(passwordEncoder.matches(TEST_NEW_PASSWORD,
                userRepository.findUserByUsername(TEST_USERNAME).get().getPassword()));
    }

    @Test
    @WithMockUser(TEST_USERNAME)
    public void deleteUserAccount() throws Exception {
        User user = new User();
        user.setUsername(TEST_USERNAME);
        user.setPassword(TEST_PASSWORD);

        userRepository.save(user);

        mockMvc.perform(delete("/api/account/delete"))
                .andExpect(status().isOk());

        assertNull(userRepository.findUserByUsername(TEST_USERNAME).orElse(null));
    }

    @Test
    @WithMockUser(UserObjectMother.TEST_USERNAME)
    public void updateUserAccount() throws Exception {
        User user = userObjectMother.createDefaultUser();

        userRepository.save(user);

        UpdateUserDto updateUserDto = new UpdateUserDto();
        updateUserDto.setFirstName("test-new-firstname");
        updateUserDto.setLastName("test-new-lastname");
        updateUserDto.setFavoriteCategories(List.of("test-new-category1", "test-new-category2"));

        mockMvc.perform(post("/api/account/update")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(asJsonString(updateUserDto)))
                        .andExpect(status().isOk());

        User updatedUser = userRepository.findWithFetchUserByUsername(user.getUsername()).get();

        assertEquals(updateUserDto.getFirstName(),
                updatedUser.getFirstName());

        assertEquals(updateUserDto.getLastName(),
                updatedUser.getLastName());


        assertEquals(updateUserDto.getFavoriteCategories().size(),
                updatedUser.getFavoriteCategories().size());

    }
}
