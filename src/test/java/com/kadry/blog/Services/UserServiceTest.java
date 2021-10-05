package com.kadry.blog.Services;

import com.kadry.blog.Services.exceptions.InvalidActivationKeyException;
import com.kadry.blog.Services.exceptions.InvalidResetKeyException;
import com.kadry.blog.Services.Imp.UserServiceImp;
import com.kadry.blog.dto.UserDto;
import com.kadry.blog.model.Authority;
import com.kadry.blog.model.User;
import com.kadry.blog.payload.KeyAndPassword;
import com.kadry.blog.repositories.AuthorityRepository;
import com.kadry.blog.repositories.UserRepository;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;


import java.util.Optional;


import static org.junit.jupiter.api.Assertions.*;
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

    @Captor
    ArgumentCaptor<User> captor;

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
        verify(userRepository).save(captor.capture());
        assertTrue(captor.getValue().isActivated());
    }

    @Test(expected = InvalidActivationKeyException.class)
    public void testActivateUserWithInvalidActivationUser() {
        userService.activateUser(TEST_ACTIVATION_KEY);
    }

    @Test
    public void testResetPasswordInit() {
        User user = new User();
        user.setActivated(true);

        when(userRepository.findUserByEmail(anyString())).thenReturn(Optional.of(user));

        Optional<User> returnedUser = userService.resetPasswordInit("test@domain.com");

        assertNotNull(returnedUser.get().getResetDate());
        assertNotNull(returnedUser.get().getResetKey());
    }

    @Test
    public void testResetPasswordFinal() {
        final String TEST_RESET_KEY = "test-reset-key";
        final String NEW_PASSWORD = "new-test-password";
        User user = new User();
        user.setPassword("test-password");
        user.setResetKey(TEST_RESET_KEY);
        when(userRepository.findUserByResetKey(anyString())).thenReturn(Optional.of(user));

        KeyAndPassword keyAndPassword = new KeyAndPassword();
        keyAndPassword.setKey(TEST_RESET_KEY);
        keyAndPassword.setPassword(NEW_PASSWORD);

        userService.resetPasswordFinal(keyAndPassword);

        verify(userRepository).save(captor.capture());
        assertEquals(NEW_PASSWORD, captor.getValue().getPassword());
    }

    @Test(expected = InvalidResetKeyException.class)
    public void testResetPasswordFinalWithInvalidKey() {
        when(userRepository.findUserByResetKey(anyString())).thenReturn(Optional.empty());
        KeyAndPassword keyAndPassword = new KeyAndPassword();
        keyAndPassword.setKey("test-key");
        keyAndPassword.setPassword("test-password");

        userService.resetPasswordFinal(keyAndPassword);
    }
}