package com.kadry.blog.Services;

import com.kadry.blog.dto.user.PasswordChangedDto;
import com.kadry.blog.dto.user.UpdateUserDto;
import com.kadry.blog.dto.user.UserDto;
import com.kadry.blog.model.User;
import com.kadry.blog.payload.KeyAndPassword;

import java.util.Optional;

public interface UserService {
     User registerNewUser(UserDto userDto);

     void activateUser(String activationKey);

     Optional<User> resetPasswordInit(String email);

     void resetPasswordFinal(KeyAndPassword keyAndPassword);

     void changePassword(PasswordChangedDto passwordChangedDto);

     void deleteUserAccount();

     void updateUser(UpdateUserDto updateUserDto);
}
