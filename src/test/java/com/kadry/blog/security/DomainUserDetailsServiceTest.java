package com.kadry.blog.security;

import com.kadry.blog.Services.exceptions.UnActivatedUserException;
import com.kadry.blog.model.Authority;
import com.kadry.blog.repositories.UserRepository;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import org.springframework.security.core.userdetails.User;

import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class DomainUserDetailsServiceTest {

    private static final String TEST_USERNAME = "test-username";
    UserDetailsService userDetailsService;

    @Mock
    UserRepository userRepository;

    @Before
    public void setUp() throws Exception {
        userDetailsService = new DomainUserDetailsService(userRepository);
    }

    @Test
    public void testLoadUserByUsername() {
        com.kadry.blog.model.User user = new com.kadry.blog.model.User();
        user.setUsername(TEST_USERNAME);
        user.setPassword("test-password");
        user.setAuthorities(Set.of(new Authority(AuthoritiesConstants.USER)));
        user.setActivated(true);

        when(userRepository.findUserByUsername(anyString())).thenReturn(Optional.of(user));

        User userDetails = (User) userDetailsService.loadUserByUsername(TEST_USERNAME);

        verify(userRepository).findUserByUsername(anyString());
        assertNotNull(userDetails);
        assertEquals(TEST_USERNAME, userDetails.getUsername());

    }

    @Test(expected = UsernameNotFoundException.class)
    public void testLoadUserWithNonExistentUsername() {

        when(userRepository.findUserByUsername(anyString())).thenReturn(Optional.empty());

        userDetailsService.loadUserByUsername(TEST_USERNAME);
    }

    @Test(expected = UnActivatedUserException.class)
    public void testLoadUserWithUnActivatedUser() {
        com.kadry.blog.model.User user = new com.kadry.blog.model.User();
        user.setActivated(false);

        when(userRepository.findUserByUsername(anyString())).thenReturn(Optional.of(user));

        userDetailsService.loadUserByUsername("test-username");
    }
}