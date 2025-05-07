package com.panjx.clouddrive.service;

import com.panjx.clouddrive.pojo.Result;
import com.panjx.clouddrive.pojo.User;
import com.panjx.clouddrive.pojo.UserDTO;
import com.panjx.clouddrive.pojo.request.UpdatePasswordRequest;
import com.panjx.clouddrive.pojo.request.UpdateUserInfoRequest;

import org.springframework.stereotype.Service;

@Service
public interface UserService {

    /**
     * 根据用户名查找用户
     * @param username 用户名
     * @return 用户对象，不存在则返回null
     */
    User findByUsername(String username);

    /**
     * 根据用户ID查找用户
     * @param userId 用户ID
     * @return 用户对象，不存在则返回null
     */
    User findById(Long userId);

    /**
     * 用户注册（完整流程，包括用户名检查）
     * @param userDTO 用户数据传输对象
     * @return 处理结果，包含访问令牌和刷新令牌或错误信息
     */
    Result register(UserDTO userDTO);
    
    /**
     * 获取当前登录用户信息
     * @return 处理结果，包含用户信息或错误消息
     */
    Result getCurrentUserInfo();
    
    /**
     * 更新用户信息
     * @param request 更新用户信息请求对象
     * @return 处理结果，包含更新后的用户信息或错误消息
     */
    Result updateUserInfo(UpdateUserInfoRequest request);
    
    /**
     * 修改用户密码
     * @param request 修改密码请求对象
     * @return 处理结果，包含成功或错误消息
     */
    Result updatePassword(UpdatePasswordRequest request);
}
