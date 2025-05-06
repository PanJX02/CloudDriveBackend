package com.panjx.clouddrive.service.user;

import com.panjx.clouddrive.pojo.UserDTO;
import com.panjx.clouddrive.pojo.response.TokenResponse;

public interface UserRegistrationService {
    /**
     * 用户注册
     * @param userDTO 用户数据传输对象
     * @return 包含访问令牌和刷新令牌的响应
     */
    TokenResponse register(UserDTO userDTO);
} 