package com.kadry.blog.object_mother;

import com.kadry.blog.model.Category;
import com.kadry.blog.model.Post;
import com.kadry.blog.model.User;

import java.util.Date;
import java.util.UUID;

public class PostObjectMother {
    private final UserObjectMother userObjectMother;

    public PostObjectMother() {
        this.userObjectMother = new UserObjectMother();
    }

    public Post createDefaultPost(){
        Post post = new Post();
        post.setUuid(UUID.randomUUID().toString());
        post.setTitle("test-title");
        post.setBody("test-body");
        post.setPostDate(new Date());
//        post.setUser(userObjectMother.createDefaultUser());
        return post;
    }
}
