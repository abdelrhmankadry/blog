package com.kadry.blog.Services;

import com.kadry.blog.dto.UserDto;
import com.kadry.blog.model.User;

public interface UserService {
     User registerNewUser(UserDto userDto);

     void activateUser(String activationKey);
}
