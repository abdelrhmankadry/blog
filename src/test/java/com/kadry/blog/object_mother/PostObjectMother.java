package com.kadry.blog.object_mother;

import com.kadry.blog.model.Category;
import com.kadry.blog.model.Post;
import com.kadry.blog.model.User;

import java.util.Date;
import java.util.UUID;

public class PostObjectMother {

    private Post post;

    public PostObjectMother createDefaultPost(){
        post = new Post();
        post.setUuid(UUID.randomUUID().toString());
        post.setTitle("test-title");
        post.setBody("test-body");
        post.setPostDate(new Date());

        return this;
    }

    public PostObjectMother buildPost(){
        post = new Post();
        return this;
    }

    public PostObjectMother uuid(String uuid){
        post.setUuid(uuid);
        return this;
    }

    public PostObjectMother title(String title){
        post.setTitle(title);
        return this;
    }

    public PostObjectMother body(String body){
        post.setBody(body);
        return this;
    }

    public PostObjectMother date(Date date){
        post.setPostDate(date);
        return this;
    }

    public PostObjectMother category(Category category){
        post.setCategory(category);
        return this;
    }

    public PostObjectMother user(User user){
        post.setUser(user);
        return this;
    }

    public Post get(){
        return post;
    }
}
