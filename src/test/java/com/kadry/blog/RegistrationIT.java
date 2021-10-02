package com.kadry.blog;

import com.kadry.blog.dto.UserDto;
import com.kadry.blog.model.User;
import com.kadry.blog.repositories.UserRepository;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit4.SpringRunner;


import static org.junit.jupiter.api.Assertions.*;


@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
public class RegistrationIT {

    private static final String TEST_USERNAME = "test_username";
    private static final String TEST_ACTIVATION_KEY = "test_activation_key";
    private static final String BASE_URL = "http://127.0.0.1:8080";
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
        userDTO.setPassword("test_password");
        userDTO.setEmail("test_email@testdomain.com");
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
        user.setPassword("test_password");
        user.setActivationKey(TEST_ACTIVATION_KEY);
        user.setActivated(false);

        userRepository.save(user);

        ResponseEntity<String> response = testRestTemplate.getForEntity(BASE_URL+"/api/activate?key="+TEST_ACTIVATION_KEY,
                String.class);


        assertEquals(HttpStatus.OK, response.getStatusCode());
        userRepository.findUserByUsername(TEST_USERNAME).ifPresent(returnedUser ->{
            assertTrue(returnedUser.isActivated());
        });


    }
}
