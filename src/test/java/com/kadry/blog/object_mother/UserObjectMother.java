package com.kadry.blog.object_mother;

import com.kadry.blog.model.User;

import java.util.List;

public class UserObjectMother {

    public static final String TEST_USERNAME = "test-username";
    public static final String TEST_ACTIVATION_KEY = "test_activation_key";
    public static final String TEST_PASSWORD = "test-password";
    public static final String TEST_EMAIL = "test_email@testdomain.com";
    public static final String FIRST_NAME = "test-firstname";
    public static final String LAST_NAME = "test-lastname";

    private  User user;

    public UserObjectMother() {
        this.user = createDefaultUser();
    }

    public  User createDefaultUser(){
        User user = new User();
        user.setUsername(TEST_USERNAME);
        user.setPassword(TEST_PASSWORD);
        user.setFirstName(FIRST_NAME);
        user.setLastName(LAST_NAME);
        user.setActivated(true);
        user.setEmail(TEST_EMAIL);
        user.setFavoriteCategories(List.of("test-categories1"));
        return user;
    }

    public UserObjectMother buildUser(){
        user = createDefaultUser();
        return this;
    }

    public UserObjectMother username(String username){
        user.setUsername(username);
        return this;
    }

    public UserObjectMother password(String password){
        user.setPassword(password);
        return this;
    }

    public UserObjectMother firstName(String firstname){
        user.setFirstName(firstname);
        return this;
    }

    public UserObjectMother lastName(String lastname){
        user.setLastName(lastname);
        return this;
    }

    public UserObjectMother activated(boolean activation){
        user.setActivated(activation);
        return this;
    }

    public UserObjectMother email(String email){
        user.setEmail(email);
        return this;
    }

    public User get(){
        return this.user;
    }

}
