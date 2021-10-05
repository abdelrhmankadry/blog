package com.kadry.blog.integration_tests;

import com.kadry.blog.dto.UserDto;
import com.kadry.blog.model.User;
import com.kadry.blog.payload.KeyAndPassword;
import com.kadry.blog.repositories.UserRepository;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;


import static org.junit.jupiter.api.Assertions.*;


@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
public class AccountResourceIT {

    private static final String TEST_USERNAME = "test_username";
    private static final String TEST_ACTIVATION_KEY = "test_activation_key";
    private static final String BASE_URL = "http://127.0.0.1:8080";
    private static final String TEST_PASSWORD = "test_password";
    private static final String TEST_EMAIL = "test_email@testdomain.com";
    public static final String NEW_TEST_PASSWORD = "new_test_password";
    @Autowired
    UserRepository userRepository;

    TestRestTemplate testRestTemplate;

    @Before
    public void setUp() throws Exception {
        testRestTemplate = new TestRestTemplate();
    }

    @Test
    public void testRegisterNewUser() {
        UserDto userDTO = new UserDto();
        userDTO.setUsername(TEST_USERNAME);
        userDTO.setPassword(TEST_PASSWORD);
        userDTO.setEmail(TEST_EMAIL);
        userDTO.setFirstName("test_firstname");
        userDTO.setLastName("test_lastname");

        ResponseEntity<String> response = testRestTemplate.postForEntity(BASE_URL+"/api/register",
                userDTO, String.class);

        assertEquals(response.getStatusCode(), HttpStatus.CREATED);

        assertTrue(userRepository.findUserByUsername(TEST_USERNAME).isPresent());
    }

    @Test
    public void testActivateAccount() {
        User user = new User();
        user.setUsername(TEST_USERNAME);
        user.setPassword(TEST_PASSWORD);
        user.setActivationKey(TEST_ACTIVATION_KEY);
        user.setActivated(false);

        userRepository.save(user);

        ResponseEntity<String> response =
                testRestTemplate.getForEntity(BASE_URL+"/api/activate?key="+TEST_ACTIVATION_KEY, String.class);


        assertEquals(HttpStatus.OK, response.getStatusCode());
        userRepository.findUserByUsername(TEST_USERNAME).ifPresent(returnedUser ->{
            assertTrue(returnedUser.isActivated());
        });
    }

    @Test
    public void testResetPassword() {
        User user = new User();
        user.setUsername(TEST_USERNAME);
        user.setPassword(TEST_PASSWORD);
        user.setEmail(TEST_EMAIL);
        user.setActivated(true);

        userRepository.save(user);

        ResponseEntity<String> passwordResetInitialResponse =
                testRestTemplate.postForEntity(BASE_URL+"/api/account/reset-password/init",
                        TEST_EMAIL, String.class);

        assertEquals(HttpStatus.OK, passwordResetInitialResponse.getStatusCode());
        User returnedUser = userRepository.findUserByEmail(TEST_EMAIL).get();
        assertNotNull(returnedUser.getResetKey());
        assertNotNull(returnedUser.getResetDate());

        KeyAndPassword keyAndPassword = new KeyAndPassword();
        keyAndPassword.setKey(returnedUser.getResetKey());
        keyAndPassword.setPassword(NEW_TEST_PASSWORD);
        ResponseEntity<String> passwordResetFinalResponse =
                testRestTemplate.postForEntity(BASE_URL+"/api/account/reset-password/final", keyAndPassword,
                        String.class);

        assertEquals(HttpStatus.OK, passwordResetFinalResponse.getStatusCode());

        returnedUser = userRepository.findUserByEmail(TEST_EMAIL).get();
        assertEquals(NEW_TEST_PASSWORD, returnedUser.getPassword());
    }
}
