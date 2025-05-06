package com.panjx.clouddrive.service.impl;

import com.panjx.clouddrive.pojo.Result;
import com.panjx.clouddrive.pojo.User;
import com.panjx.clouddrive.pojo.UserDTO;
import com.panjx.clouddrive.pojo.request.UpdatePasswordRequest;
import com.panjx.clouddrive.pojo.request.UpdateUserInfoRequest;
import com.panjx.clouddrive.pojo.response.TokenResponse;
import com.panjx.clouddrive.service.UserService;
import com.panjx.clouddrive.service.user.UserInfoService;
import com.panjx.clouddrive.service.user.UserRegistrationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * 用户服务实现类 - 使用门面模式整合其他用户相关服务
 */
@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserInfoService userInfoService;
    
    @Autowired
    private UserRegistrationService userRegistrationService;

    @Override
    public User findByUsername(String username) {
        return userInfoService.findByUsername(username);
    }

    @Override
    public User findById(Long userId) {
        return userInfoService.findById(userId);
    }

    @Override
    public TokenResponse register(UserDTO userDTO) {
        return userRegistrationService.register(userDTO);
    }
    
    @Override
    public Result getCurrentUserInfo() {
        return userInfoService.getCurrentUserInfo();
    }
    
    @Override
    public Result updateUserInfo(UpdateUserInfoRequest request) {
        return userInfoService.updateUserInfo(request);
    }
    
    @Override
    public Result updatePassword(UpdatePasswordRequest request) {
        return userInfoService.updatePassword(request);
    }
}
