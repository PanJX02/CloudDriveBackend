package com.panjx.clouddrive.service.impl;

import com.panjx.clouddrive.mapper.AuthMapper;
import com.panjx.clouddrive.pojo.Result;
import com.panjx.clouddrive.pojo.User;
import com.panjx.clouddrive.pojo.UserDTO;
import com.panjx.clouddrive.service.AuthService;
import com.panjx.clouddrive.service.UserService;
import com.panjx.clouddrive.utils.JwtUtil;
import com.panjx.clouddrive.utils.PasswordUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


@Service
public class AuthServiceImpl implements AuthService {

    @Autowired
    private UserService userService;

    @Autowired
    private AuthMapper authMapper;


    @Override
    public Result authenticate(UserDTO userDTO) {
        //查询是否有该用户
        User user = userService.findByUsername(userDTO.getUsername());
        if (user==null){
            //用户不存在
            return Result.error("用户名或密码错误");
        }
        if (PasswordUtil.matches(userDTO.getPassword(),user.getPassword())){
            //检查用户是否被禁用
            if (user.getStatus()==0){
                return Result.error("用户被禁用");
            }

            authMapper.updateLoginTime(user.getUserName(),System.currentTimeMillis());
            //密码正确且没禁用，生成JWT令牌
            return Result.success(JwtUtil.generateToken(user));
        }
        //密码错误
        return Result.error("用户名或密码错误");
    }
}
