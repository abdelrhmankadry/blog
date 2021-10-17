package com.kadry.blog.Services;

import com.kadry.blog.Services.exceptions.InvalidActivationKeyException;
import com.kadry.blog.Services.exceptions.InvalidResetKeyException;
import com.kadry.blog.Services.Imp.UserServiceImp;
import com.kadry.blog.Services.exceptions.UnAuthenticatedAccessException;
import com.kadry.blog.dto.PasswordChangedDto;
import com.kadry.blog.dto.UpdateUserDto;
import com.kadry.blog.dto.UserDto;
import com.kadry.blog.model.Authority;
import com.kadry.blog.model.User;
import com.kadry.blog.payload.KeyAndPassword;
import com.kadry.blog.repositories.AuthorityRepository;
import com.kadry.blog.repositories.UserRepository;
import com.kadry.blog.security.SecurityUtils;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.*;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.test.context.support.WithMockUser;


import java.util.List;
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
    ArgumentCaptor<User> userCaptor;

    @Captor
    ArgumentCaptor<String> stringCaptor;

    UserService userService;

    PasswordEncoder passwordEncoder;
    @Before
    public void setUp() throws Exception {
        passwordEncoder = new BCryptPasswordEncoder();
        userService = new UserServiceImp(userRepository, authorityRepository, passwordEncoder);

    }

    @BeforeClass
    public static void beforeClass() throws Exception {
        mockStatic(SecurityUtils.class);
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
        verify(userRepository).save(userCaptor.capture());
        assertTrue(userCaptor.getValue().isActivated());
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

        verify(userRepository).save(userCaptor.capture());
        assertEquals(NEW_PASSWORD, userCaptor.getValue().getPassword());
    }

    @Test(expected = InvalidResetKeyException.class)
    public void testResetPasswordFinalWithInvalidKey() {
        when(userRepository.findUserByResetKey(anyString())).thenReturn(Optional.empty());
        KeyAndPassword keyAndPassword = new KeyAndPassword();
        keyAndPassword.setKey("test-key");
        keyAndPassword.setPassword("test-password");

        userService.resetPasswordFinal(keyAndPassword);
    }

    @Test
    public void testChangePassword()  {
        final String TEST_PASSWORD = "test-current_password";
        final String TEST_NEW_PASSWORD = "test-new_password";

        User user = new User();
        user.setUsername("test-username");
        user.setPassword(passwordEncoder.encode(TEST_PASSWORD));

        PasswordChangedDto passwordChangedDto = new PasswordChangedDto();
        passwordChangedDto.setCurrentPassword(TEST_PASSWORD);
        passwordChangedDto.setNewPassword(TEST_NEW_PASSWORD);


        when(SecurityUtils.getCurrentUserLogin()).thenReturn(Optional.of("test-username"));
        when(userRepository.findUserByUsername(anyString())).thenReturn(Optional.of(user));

        userService.changePassword(passwordChangedDto);

        verify(userRepository).save(userCaptor.capture());
        User passedUser = userCaptor.getValue();
        assertTrue(passwordEncoder.matches(TEST_NEW_PASSWORD, passedUser.getPassword()));
    }

    @Test(expected = BadCredentialsException.class)
    public void testChangePasswordWithInvalidCurrentPassword() {

        User user = new User();
        user.setUsername("test-username");
        user.setPassword("test-password");

        PasswordChangedDto passwordChangedDto = new PasswordChangedDto();
        passwordChangedDto.setCurrentPassword("invalid-password");
        passwordChangedDto.setCurrentPassword("test-new_password");


        when(SecurityUtils.getCurrentUserLogin()).thenReturn(Optional.of("test-username"));
        when(userRepository.findUserByUsername(anyString())).thenReturn(Optional.of(user));

        userService.changePassword(passwordChangedDto);
    }

    @Test(expected = UnAuthenticatedAccessException.class)
    public void testUnAuthenticatedAccessToChangePassword() {
        when(SecurityUtils.getCurrentUserLogin()).thenReturn(Optional.empty());

        userService.changePassword(new PasswordChangedDto());
    }

    @Test
    public void deleteUserAccount() {
        when(SecurityUtils.getCurrentUserLogin()).thenReturn(Optional.of("test-username"));
        userService.deleteUserAccount();

        verify(userRepository).deleteUserByUsername(stringCaptor.capture());

        assertEquals("test-username", stringCaptor.getValue());

    }

    @Test
    public void updateUserAccount() {
        when(SecurityUtils.getCurrentUserLogin()).thenReturn(Optional.of("test-username"));
        when(userRepository.findUserWithFavoriteCategoriesByUsername(any())).thenReturn(Optional.of(new User()));
        UpdateUserDto updateUserDto = getUpdateUserDto();

        userService.updateUser(updateUserDto);

        verify(userRepository).save(userCaptor.capture());
        User updatedUser = userCaptor.getValue();
        assertEquals(updateUserDto.getFirstName(), updatedUser.getFirstName());
        assertEquals(updateUserDto.getLastName(), updatedUser.getLastName());
        assertEquals(updateUserDto.getFavoriteCategories().size(), updatedUser.getFavoriteCategories().size());
    }

    private UpdateUserDto getUpdateUserDto() {
        UpdateUserDto updateUserDto = new UpdateUserDto();
        updateUserDto.setFirstName("test-new-firstname");
        updateUserDto.setLastName("test-new-lastname");
        updateUserDto.setFavoriteCategories(List.of("new-category1", "new-category2"));
        return updateUserDto;
    }
}