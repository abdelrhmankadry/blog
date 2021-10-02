package com.kadry.blog.Services;

import com.kadry.blog.Services.Imp.InvalidActivationKey;
import com.kadry.blog.Services.Imp.UserServiceImp;
import com.kadry.blog.dto.UserDto;
import com.kadry.blog.model.Authority;
import com.kadry.blog.model.User;
import com.kadry.blog.repositories.AuthorityRepository;
import com.kadry.blog.repositories.UserRepository;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;


import java.util.Optional;


import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class UserServiceTest {

    private static final String TEST_ACTIVATION_KEY = "test_activation_key";

    @Mock
    UserRepository userRepository;

    @Mock
    AuthorityRepository authorityRepository;


    UserService userService;

    @Before
    public void setUp() throws Exception {
     userService = new UserServiceImp(userRepository, authorityRepository, new BCryptPasswordEncoder());
    }

    @Test
    public void testCreateNewUser() {
        Optional<Authority> authority = Optional.of(new Authority("USER"));
        when(authorityRepository.findAuthorityByName(anyString())).thenReturn(authority);

        UserDto userDto = new UserDto();
        userDto.setUsername("test_username");
        userDto.setPassword("test_password");
        userDto.setEmail("test_email");
        userDto.setFirstName("test_firstname");
        userDto.setLastName("test_lastname");

        userService.registerNewUser(userDto);

        verify(userRepository).save(any(User.class));
    }

    @Test
    public void testActivateUser() {
        when(userRepository.findUserByActivationKey(anyString())).thenReturn(Optional.of(new User()));
        userService.activateUser(TEST_ACTIVATION_KEY);
        verify(userRepository).findUserByActivationKey(TEST_ACTIVATION_KEY);
    }

    @Test(expected = InvalidActivationKey.class)
    public void testActivateUserWithInvalidActivationUser() {
        userService.activateUser(TEST_ACTIVATION_KEY);
    }
}