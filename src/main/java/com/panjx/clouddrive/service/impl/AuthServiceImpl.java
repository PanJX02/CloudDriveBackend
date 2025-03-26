package com.panjx.clouddrive.service.impl;

import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.panjx.clouddrive.mapper.AuthMapper;
import com.panjx.clouddrive.pojo.Result;
import com.panjx.clouddrive.pojo.TokenResponse;
import com.panjx.clouddrive.pojo.User;
import com.panjx.clouddrive.pojo.UserDTO;
import com.panjx.clouddrive.service.AuthService;
import com.panjx.clouddrive.service.UserService;
import com.panjx.clouddrive.utils.JwtUtil;
import com.panjx.clouddrive.utils.PasswordUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


@Slf4j
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
            log.info("用户登录：{}",user.getUserName());
            authMapper.updateLoginTime(user.getUserName(),System.currentTimeMillis());
            
            //生成访问令牌和刷新令牌
            String accessToken = JwtUtil.generateToken(user);
            String refreshToken = JwtUtil.generateRefreshToken(user);
            TokenResponse tokenResponse = new TokenResponse(accessToken, refreshToken);
            
            //返回令牌
            return Result.success(tokenResponse);
        }
        //密码错误
        return Result.error("用户名或密码错误");
    }
    
    @Override
    public Result refreshToken(String refreshToken) {
        try {
            // 验证刷新令牌
            if (!JwtUtil.isRefreshToken(refreshToken)) {
                return Result.error("无效的刷新令牌");
            }
            
            // 获取用户信息
            DecodedJWT decodedJWT = JwtUtil.verifyToken(refreshToken);
            Long userId = Long.parseLong(decodedJWT.getSubject());
            String username = decodedJWT.getClaim("username").asString();
            
            // 查询用户
            User user = userService.findByUsername(username);
            if (user == null || !user.getUserId().equals(userId)) {
                return Result.error("用户不存在或用户信息已变更");
            }
            
            // 检查用户状态
            if (user.getStatus() == 0) {
                return Result.error("用户已被禁用");
            }
            
            // 生成新的访问令牌和刷新令牌
            String newAccessToken = JwtUtil.generateToken(user);
            //String newRefreshToken = JwtUtil.generateRefreshToken(user);
            TokenResponse tokenResponse = new TokenResponse(newAccessToken);
            
            return Result.success(tokenResponse);
        } catch (JWTVerificationException e) {
            log.error("刷新令牌验证失败: ", e);
            return Result.error("刷新令牌已过期或无效");
        } catch (Exception e) {
            log.error("刷新令牌处理异常: ", e);
            return Result.error("刷新令牌处理失败");
        }
    }
}
