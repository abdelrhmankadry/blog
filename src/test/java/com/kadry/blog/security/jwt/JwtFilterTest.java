package com.kadry.blog.security.jwt;



import org.hibernate.mapping.Any;
import org.junit.Test;
import org.junit.Before;
import org.junit.runner.RunWith;
import org.mockito.Mock;


import org.mockito.junit.MockitoJUnitRunner;

import org.springframework.http.HttpStatus;
import org.springframework.mock.web.MockFilterChain;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;


import javax.servlet.ServletException;
import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class JwtFilterTest {
    private JwtFilter jwtFilter;

    @Mock
    TokenProvider tokenProvider;

    @Before
    public void setUp() {
        jwtFilter = new JwtFilter(tokenProvider);
    }

    @Test
     public void testJwtFilter() throws ServletException, IOException {
        when(tokenProvider.validate(anyString())).thenReturn(true);
        when(tokenProvider.getAuthentication(anyString()))
                .thenReturn(new UsernamePasswordAuthenticationToken("test-principle", "test-credential"));
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addHeader("Authorization","Bearer test-jwt");
        MockHttpServletResponse response = new MockHttpServletResponse();
        MockFilterChain filterChain = new MockFilterChain();
        jwtFilter.doFilter(request, response, filterChain);

        assertEquals(HttpStatus.OK.value(), response.getStatus());
        verify(tokenProvider).validate(anyString());
    }

    @Test
     public void testJwtFilterWithInvalidJwt() throws ServletException, IOException {
        when(tokenProvider.validate(anyString())).thenReturn(false);
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addHeader("Authorization", "Bearer test-jwt");
        MockHttpServletResponse response = new MockHttpServletResponse();
        MockFilterChain filterChain = new MockFilterChain();
        jwtFilter.doFilter(request, response, filterChain);

        assertEquals(HttpStatus.FORBIDDEN.value(), response.getStatus());
    }
}