package com.kadry.blog.repositories;

import com.kadry.blog.model.User;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;


public interface UserRepository extends CrudRepository<User, Long> {


    Optional<User> findUserByUsername( String username);

    @Query("SELECT u " +
            "FROM User u " +
            "JOIN FETCH u.favoriteCategories " +
            "WHERE u.username = :username")
    Optional<User> findWithFetchUserByUsername(@Param("username") String username);

    Optional<User> findUserByEmail(String email);

    Optional<User> findUserByActivationKey(String activationKey);

    Optional<User> findUserByResetKey(String resetKey);

    void deleteUserByUsername(String username);

    void flush();
}
