package com.panjx.clouddrive.service.impl;

import com.panjx.clouddrive.mapper.UserMapper;
import com.panjx.clouddrive.pojo.response.TokenResponse;
import com.panjx.clouddrive.pojo.User;
import com.panjx.clouddrive.pojo.UserDTO;
import com.panjx.clouddrive.service.UserService;
import com.panjx.clouddrive.utils.JwtUtil;
import com.panjx.clouddrive.utils.PasswordUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserMapper userMapper;

    // 根据用户名查询用户
    @Override
    public User findByUsername(String username) {
        return userMapper.findByUsername(username);
    }

    // 根据用户ID查询用户
    @Override
    public User findById(Long userId) {
        return userMapper.findById(userId);
    }

    // 注册
    @Override
    public TokenResponse register(UserDTO userDTO) {
        //加密
        String password = PasswordUtil.encode(userDTO.getPassword());

        //插入数据库
        // 时间戳
        long timestamp = System.currentTimeMillis();
        // 将时间戳转换为Date对象
        User u = new User(null, userDTO.getUsername(), userDTO.getUsername(), userDTO.getEmail(), 0, null, password, timestamp, timestamp, 0, 0L, null);
        userMapper.add(u);
        User user = userMapper.findByUsername(userDTO.getUsername());
        //生成访问令牌和刷新令牌
        String accessToken = JwtUtil.generateToken(user);
        String refreshToken = JwtUtil.generateRefreshToken(user);
        TokenResponse tokenResponse = new TokenResponse(accessToken, refreshToken);

        //返回令牌
        return tokenResponse;
    }

}
