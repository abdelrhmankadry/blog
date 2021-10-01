package com.kadry.blog.Services;

import com.kadry.blog.Services.Imp.RegisterServiceImp;
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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;


import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class RegisterServiceTest {

    @Mock
    UserRepository userRepository;

    @Mock
    AuthorityRepository authorityRepository;


    RegisterService registerService;

    @Before
    public void setUp() throws Exception {
     registerService = new RegisterServiceImp(userRepository, authorityRepository, new BCryptPasswordEncoder());
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

        registerService.registerNewUser(userDto);

        verify(userRepository).save(any(User.class));
    }
}