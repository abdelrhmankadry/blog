package com.kadry.blog.Services.Imp;

import com.kadry.blog.Services.exceptions.EmailAlreadyUsedException;
import com.kadry.blog.Services.UserService;
import com.kadry.blog.Services.exceptions.UsernameAlreadyUsedException;
import com.kadry.blog.Services.exceptions.InvalidActivationKeyException;
import com.kadry.blog.Services.exceptions.InvalidResetKeyException;
import com.kadry.blog.dto.PasswordChangedDto;
import com.kadry.blog.dto.UserDto;
import com.kadry.blog.mapper.UserMapper;
import com.kadry.blog.model.Authority;
import com.kadry.blog.model.User;
import com.kadry.blog.payload.KeyAndPassword;
import com.kadry.blog.repositories.AuthorityRepository;
import com.kadry.blog.repositories.UserRepository;
import com.kadry.blog.security.AuthoritiesConstants;
import com.kadry.blog.security.RandomUtil;
import com.kadry.blog.security.SecurityUtils;
import com.kadry.blog.Services.exceptions.UnAuthenticatedAccessException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

@Service
public class UserServiceImp implements UserService {

    private final UserRepository userRepository;
    private final AuthorityRepository authorityRepository;
    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;
    public UserServiceImp(UserRepository userRepository, AuthorityRepository authorityRepository, PasswordEncoder passwordEncoder) {
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

    @Override
    public void activateUser(String activationKey) throws InvalidActivationKeyException {
        Optional<User> optionalUser = userRepository.findUserByActivationKey(activationKey);
        if(! optionalUser.isPresent()){
            throw new InvalidActivationKeyException();
        }

        optionalUser.ifPresent(user -> {
            user.setActivated(true);
            userRepository.save(user);
        });
    }

    @Override
    public Optional<User> resetPasswordInit(String email) {
        Optional<User> userOptional = userRepository.findUserByEmail(email);
        if(userOptional.isPresent()){
            User user = userOptional.get();
            if (user.isActivated()) {
                user.setResetDate(Instant.now());
                user.setResetKey(RandomUtil.generateResetKey());
                userRepository.save(user);
                return Optional.of(user);
            }
        }
        return Optional.empty();
    }

    @Override
    public void resetPasswordFinal(KeyAndPassword keyAndPassword) {

        userRepository.findUserByResetKey(keyAndPassword.getKey()).ifPresentOrElse(user -> {
            user.setPassword(keyAndPassword.getPassword());
            userRepository.save(user);
        },
                ()-> {throw new InvalidResetKeyException();});
    }

    @Override
    public void changePassword(PasswordChangedDto passwordChangedDto){
        String username = SecurityUtils.getCurrentUserLogin()
                .orElseThrow(UnAuthenticatedAccessException::new);
        userRepository.findUserByUsername(username).ifPresent(user -> {
            if(passwordEncoder.matches(passwordChangedDto.getCurrentPassword(),
                    user.getPassword())){
                user.setPassword(passwordEncoder.encode(passwordChangedDto.getNewPassword()));
                userRepository.save(user);
            }else{
                throw new BadCredentialsException("Invalid current password!");
            }
        });
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
