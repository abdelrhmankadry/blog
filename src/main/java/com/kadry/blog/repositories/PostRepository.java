package com.kadry.blog.repositories;


import com.kadry.blog.model.Post;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;


import java.util.Optional;

public interface PostRepository extends CrudRepository<Post, Long> {

    Optional<Post> findPostByTitle(String title);
    Optional<Post> findPostByUuid(String Uuid);

    @Query("SELECT p "+
            "FROM Post p " +
            "JOIN FETCH p.category "+
            "WHERE p.uuid = :uuid")
    Optional<Post> findEagerlyPostByUuid(@Param("uuid") String uuid);

    void flush();
}
