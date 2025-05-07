package com.panjx.clouddrive.service.user.impl;

import com.panjx.clouddrive.config.InviteCodeConfig;
import com.panjx.clouddrive.mapper.UserMapper;
import com.panjx.clouddrive.pojo.InvitationCode;
import com.panjx.clouddrive.pojo.Result;
import com.panjx.clouddrive.pojo.User;
import com.panjx.clouddrive.pojo.UserDTO;
import com.panjx.clouddrive.pojo.response.TokenResponse;
import com.panjx.clouddrive.service.InvitationCodeService;
import com.panjx.clouddrive.service.user.UserRegistrationService;
import com.panjx.clouddrive.utils.JwtUtil;
import com.panjx.clouddrive.utils.PasswordUtil;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

@Service
public class UserRegistrationServiceImpl implements UserRegistrationService {

    @Autowired
    private UserMapper userMapper;
    
    @Autowired
    private InvitationCodeService invitationCodeService;
    
    @Autowired
    private InviteCodeConfig inviteCodeConfig;

    @Override
    @Transactional
    public Result register(UserDTO userDTO) {
        // 先检查用户名是否已存在
        User existingUser = userMapper.findByUsername(userDTO.getUsername());
        if (existingUser != null) {
            return Result.error("用户名已存在");
        }
        
        //加密
        String password = PasswordUtil.encode(userDTO.getPassword());

        // 检查邀请码并获取用户身份
        Integer identity = 0; // 默认普通用户身份
        InvitationCode invitationCode = null;
        
        // 根据配置检查是否必须提供邀请码
        if (inviteCodeConfig.isRequired() && (userDTO.getInviteCode() == null || userDTO.getInviteCode().trim().isEmpty())) {
            return Result.error("注册需要邀请码");
        }
        
        // 如果提供了邀请码，验证邀请码
        if (userDTO.getInviteCode() != null && !userDTO.getInviteCode().trim().isEmpty()) {
            invitationCode = invitationCodeService.validateInviteCode(userDTO.getInviteCode());
            
            // 如果邀请码无效
            if (invitationCode == null) {
                return Result.error("邀请码无效或已过期");
            }
            
            // 使用邀请码中的身份
            identity = invitationCode.getIdentity();
        }

        try {
            //插入数据库
            // 当前时间戳，同时用作注册时间和首次登录时间
            long currentTimestamp = System.currentTimeMillis();
            
            // 创建用户对象，同时设置注册时间和最后登录时间
            User user = new User(
                null, 
                userDTO.getUsername(), 
                userDTO.getUsername(), 
                userDTO.getEmail(), 
                identity, // 从邀请码获取的身份
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
            
            // 如果使用了邀请码，记录邀请码使用情况
            if (invitationCode != null) {
                // 获取客户端IP
                HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
                String ip = getClientIp(request);
                
                // 记录邀请码使用
                boolean recordSuccess = invitationCodeService.recordInviteCodeUsage(invitationCode, registeredUser.getUserId(), ip);
                if (!recordSuccess) {
                    // 记录失败，但不影响注册流程，可以记录日志
                    System.out.println("邀请码使用记录失败: " + userDTO.getInviteCode());
                }
            }
            
            //生成访问令牌和刷新令牌
            String accessToken = JwtUtil.generateToken(registeredUser);
            String refreshToken = JwtUtil.generateRefreshToken(registeredUser);
            TokenResponse tokenResponse = new TokenResponse(accessToken, refreshToken);

            //返回成功结果
            return Result.success(tokenResponse);
        } catch (Exception e) {
            // 捕获数据库操作或其他异常
            e.printStackTrace();
            return Result.error("注册失败: " + e.getMessage());
        }
    }
    
    /**
     * 获取客户端IP地址
     */
    private String getClientIp(HttpServletRequest request) {
        String ip = request.getHeader("X-Forwarded-For");
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("Proxy-Client-IP");
        }
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("WL-Proxy-Client-IP");
        }
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("HTTP_CLIENT_IP");
        }
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("HTTP_X_FORWARDED_FOR");
        }
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getRemoteAddr();
        }
        return ip;
    }
} 