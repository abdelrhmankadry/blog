package com.kadry.blog.integration_tests;

import com.kadry.blog.security.AuthoritiesConstants;
import com.kadry.blog.security.jwt.JwtFilter;
import com.kadry.blog.security.jwt.TokenProvider;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.mock.web.MockFilterChain;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.context.junit4.SpringRunner;

import javax.servlet.ServletException;
import java.io.IOException;
import java.util.Collections;

import static org.junit.Assert.*;

@RunWith(SpringRunner.class)
@SpringBootTest
public class JwtFilterIT {

    private static final String TEST_USERNAME = "test-username";
    private static final String TEST_PASSWORD = "test-password";

    private JwtFilter jwtFilter;

    @Autowired
    private TokenProvider tokenProvider;


    @Before
    public void setUp() throws Exception {
        jwtFilter = new JwtFilter(tokenProvider);
    }

    @Test
    public void testJwtFilter() throws ServletException, IOException {
        Authentication authentication = new UsernamePasswordAuthenticationToken(TEST_USERNAME,
                TEST_PASSWORD, Collections.singletonList(new SimpleGrantedAuthority(AuthoritiesConstants.USER)));
        String jwt = tokenProvider.generateToken(authentication);

        MockHttpServletRequest request = new MockHttpServletRequest();
        MockHttpServletResponse response = new MockHttpServletResponse();
        MockFilterChain filterChain = new MockFilterChain();

        request.addHeader("Authorization", "Bearer " + jwt);
        jwtFilter.doFilter(request, response, filterChain);

        assertEquals(HttpStatus.OK.value(), response.getStatus());
        assertNotNull(SecurityContextHolder.getContext().getAuthentication());
        assertEquals(TEST_USERNAME, SecurityContextHolder.getContext().getAuthentication().getName());
        assertEquals(jwt, SecurityContextHolder.getContext().getAuthentication().getCredentials());
    }

    @Test
    public void testJwtFilterWithInvalidJwt() throws ServletException, IOException {
        String invalidJwt = "test_invalid_jwt";
        MockHttpServletRequest request = new MockHttpServletRequest();
        MockHttpServletResponse response = new MockHttpServletResponse();
        MockFilterChain filterChain = new MockFilterChain();

        request.addHeader("Authorization", "Bearer " + invalidJwt);
        jwtFilter.doFilter(request, response, filterChain);
        
        assertNull(SecurityContextHolder.getContext().getAuthentication());
    }
}
