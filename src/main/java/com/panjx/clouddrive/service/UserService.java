package com.panjx.clouddrive.service;

import com.panjx.clouddrive.pojo.TokenResponse;
import com.panjx.clouddrive.pojo.User;

import com.panjx.clouddrive.pojo.UserDTO;
import org.springframework.stereotype.Service;

@Service
public interface UserService {


    User findByUsername(String username);

    TokenResponse register(UserDTO userDTO);
}
