package com.kadry.blog.security;

import com.kadry.blog.Services.exceptions.UnActivatedUserException;
import com.kadry.blog.model.User;
import com.kadry.blog.repositories.UserRepository;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.stream.Collectors;


public class DomainUserDetailsService implements UserDetailsService {
    private final UserRepository userRepository;

    public DomainUserDetailsService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findUserByUsername(username)
                    .orElseThrow(()-> new UsernameNotFoundException("Username is not found!"));

        return createSpringSecurityUser(user);
    }

    private org.springframework.security.core.userdetails.User createSpringSecurityUser(User user) {
        if(!user.isActivated()){
            throw new UnActivatedUserException("User account is not activated yey!");
        }
        Collection<GrantedAuthority> authorities = user.getAuthorities().stream()
                .map(authority -> new SimpleGrantedAuthority(authority.getName())).
                        collect(Collectors.toList());
        return new org.springframework.security.core.userdetails
                .User(user.getUsername(), user.getPassword(), authorities);
    }
}
