package com.panjx.clouddrive.service.user;

import com.panjx.clouddrive.pojo.Result;
import com.panjx.clouddrive.pojo.UserDTO;

public interface UserRegistrationService {
    /**
     * 用户注册（包含用户名检查）
     * @param userDTO 用户数据传输对象
     * @return 包含访问令牌和刷新令牌的响应，或错误信息
     */
    Result register(UserDTO userDTO);
} 