package com.panjx.clouddrive.service.user.impl;

import com.panjx.clouddrive.mapper.UserMapper;
import com.panjx.clouddrive.pojo.User;
import com.panjx.clouddrive.pojo.UserDTO;
import com.panjx.clouddrive.pojo.response.TokenResponse;
import com.panjx.clouddrive.service.user.UserRegistrationService;
import com.panjx.clouddrive.utils.JwtUtil;
import com.panjx.clouddrive.utils.PasswordUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserRegistrationServiceImpl implements UserRegistrationService {

    @Autowired
    private UserMapper userMapper;

    @Override
    public TokenResponse register(UserDTO userDTO) {
        //加密
        String password = PasswordUtil.encode(userDTO.getPassword());

        //插入数据库
        // 当前时间戳，同时用作注册时间和首次登录时间
        long currentTimestamp = System.currentTimeMillis();
        
        // 创建用户对象，同时设置注册时间和最后登录时间
        User user = new User(
            null, 
            userDTO.getUsername(), 
            userDTO.getUsername(), 
            userDTO.getEmail(), 
            0, 
            null, 
            password, 
            currentTimestamp,  // 注册时间
            currentTimestamp,  // 最后登录时间（注册即视为首次登录）
            1,  // 状态设为正常
            0L,  // 已使用空间初始为0
            5368709120L  // 总空间默认为5GB
        );
        
        userMapper.add(user);
        User registeredUser = userMapper.findByUsername(userDTO.getUsername());
        //生成访问令牌和刷新令牌
        String accessToken = JwtUtil.generateToken(registeredUser);
        String refreshToken = JwtUtil.generateRefreshToken(registeredUser);
        TokenResponse tokenResponse = new TokenResponse(accessToken, refreshToken);

        //返回令牌
        return tokenResponse;
    }
} 