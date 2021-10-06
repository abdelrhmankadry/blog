package com.kadry.blog.repositories;

import com.kadry.blog.model.User;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;


public interface UserRepository extends CrudRepository<User, Long> {

    Optional<User> findUserByUsername(String username);

    Optional<User> findUserByEmail(String email);

    Optional<User> findUserByActivationKey(String activationKey);

    Optional<User> findUserByResetKey(String resetKey);
    void flush();
}
