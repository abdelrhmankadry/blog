package com.kadry.blog;

import com.kadry.blog.dto.UserDto;
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

    public static final String TEST_USERNAME = "test_username";
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

        ResponseEntity<String> response = testRestTemplate.postForEntity("http://127.0.0.1:8080/api/register", userDTO, String.class);

        assertEquals(response.getStatusCode(), HttpStatus.CREATED);

        assertTrue(userRepository.findUserByUsername(TEST_USERNAME).isPresent());
    }
}
