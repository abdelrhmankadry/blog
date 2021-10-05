package com.kadry.blog.config;

import com.kadry.blog.repositories.UserRepository;
import com.kadry.blog.security.DomainUserDetailsService;
import com.kadry.blog.security.jwt.JwtConfiguration;
import com.kadry.blog.security.jwt.TokenProvider;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    private final UserRepository userRepository;
    private final TokenProvider tokenProvider;

    public SecurityConfig(UserRepository userRepository, TokenProvider tokenProvider) {
        this.userRepository = userRepository;
        this.tokenProvider = tokenProvider;
    }

    @Bean
    public UserDetailsService domainUserDetailServiceBean(){
        return new DomainUserDetailsService(userRepository);
    }
    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.httpBasic();

        http.csrf().disable()
            .sessionManagement()
            .sessionCreationPolicy(SessionCreationPolicy.STATELESS);

        http.authorizeRequests()
                .mvcMatchers("/api/register").permitAll()
                .mvcMatchers("/api/activate").permitAll()
                .mvcMatchers("/api/authenticate").permitAll()
                .mvcMatchers("/api/account/reset-password/**").permitAll()
        .and()
            .apply(new JwtConfiguration(tokenProvider));


        http.headers().frameOptions().sameOrigin();
    }

    @Bean
    PasswordEncoder passwordEncoder(){
        return new BCryptPasswordEncoder();
    }

    @Override
    @Bean
    public AuthenticationManager authenticationManagerBean() throws Exception {
        return super.authenticationManagerBean();
    }
}
