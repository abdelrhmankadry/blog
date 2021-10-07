package com.kadry.blog.security;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.junit4.SpringRunner;

import static org.junit.jupiter.api.Assertions.*;

@RunWith(SpringRunner.class)
@SpringBootTest
public class SecurityUtilsTest {

    @Test
    @WithMockUser("test-user")
    public void testGetCurrentUserLogin() {
        String user = SecurityUtils.getCurrentUserLogin().orElse(null);
        assertNotNull(user);
        assertEquals("test-user", user);
    }

    @Test
    public void testGetCurrentUserLoginWhenNoAuthenticatedUser() {
        String user = SecurityUtils.getCurrentUserLogin().orElse(null);
        assertNull(user);
    }
}