package com.kadry.blog.repositories;


import com.kadry.blog.model.Post;
import org.springframework.data.repository.CrudRepository;


import java.util.Optional;

public interface PostRepository extends CrudRepository<Post, Long> {

    Optional<Post> findPostByTitle(String title);
    Optional<Post> findPostByUuid(String Uuid);
}
