package com.kadry.blog.repositories;

import com.kadry.blog.model.Authority;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

public interface AuthorityRepository extends CrudRepository<Authority, Long> {

    Optional<Authority> findAuthorityByName(String name);
}
