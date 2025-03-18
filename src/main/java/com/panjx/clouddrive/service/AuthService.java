package com.panjx.clouddrive.service;

import com.panjx.clouddrive.pojo.Result;
import com.panjx.clouddrive.pojo.User;
import com.panjx.clouddrive.pojo.UserDTO;
import jakarta.validation.Valid;
import org.springframework.stereotype.Service;

@Service
public interface AuthService {

    Result authenticate(UserDTO userDTO);
}
