package com.kadry.blog.Services.Imp;

import com.kadry.blog.Services.EmailAlreadyUsedException;
import com.kadry.blog.Services.RegisterService;
import com.kadry.blog.Services.UsernameAlreadyUsedException;
import com.kadry.blog.dto.UserDto;
import com.kadry.blog.mapper.UserMapper;
import com.kadry.blog.model.Authority;
import com.kadry.blog.model.User;
import com.kadry.blog.repositories.AuthorityRepository;
import com.kadry.blog.repositories.UserRepository;
import com.kadry.blog.security.AuthoritiesConstants;
import com.kadry.blog.security.RandomUtil;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.Set;

@Service
public class RegisterServiceImp implements RegisterService {

    private final UserRepository userRepository;
    private final AuthorityRepository authorityRepository;
    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;
    public RegisterServiceImp(UserRepository userRepository, AuthorityRepository authorityRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.authorityRepository = authorityRepository;
        this.passwordEncoder = passwordEncoder;
        userMapper = UserMapper.INSTANCE;
    }

    @Override
    public User registerNewUser(UserDto userDto){
        userRepository.findUserByUsername(userDto.getUsername())
                .ifPresent( existingUser ->{
                        boolean removed = removeNonActivatedUser(existingUser);
                        if(!removed){
                            throw new UsernameAlreadyUsedException();
                        }
                });

        userRepository.findUserByEmail(userDto.getEmail())
                .ifPresent(existingUser -> {
                    boolean removed = removeNonActivatedUser(existingUser);
                    if(!removed){
                        throw new EmailAlreadyUsedException();
                    }
                });

        User user = userMapper.UserDtoToUser(userDto);
        user.setPassword(passwordEncoder.encode(userDto.getPassword()));

        user.setActivated(false);
        user.setActivationKey(RandomUtil.generateActivationKey());

        Set<Authority> authorities = new HashSet<>();
        authorityRepository.findAuthorityByName(AuthoritiesConstants.USER).ifPresent(authorities::add);
        user.setAuthorities(authorities);

        return userRepository.save(user);
    }

    private boolean removeNonActivatedUser(User existingUser) {
        if(existingUser.isActivated()){
            return false;
        }
        userRepository.delete(existingUser);
        userRepository.flush();
        return true;
    }
}