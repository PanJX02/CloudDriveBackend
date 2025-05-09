package com.panjx.clouddrive.service.impl;

import com.panjx.clouddrive.mapper.AdminMapper;
import com.panjx.clouddrive.pojo.Admin;
import com.panjx.clouddrive.pojo.AdminDTO;
import com.panjx.clouddrive.pojo.Result;
import com.panjx.clouddrive.pojo.response.TokenResponse;
import com.panjx.clouddrive.service.AdminLoginService;
import com.panjx.clouddrive.service.AdminQueryService;
import com.panjx.clouddrive.utils.JwtUtil;
import com.panjx.clouddrive.utils.PasswordUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class AdminLoginServiceImpl implements AdminLoginService {

    @Autowired
    private AdminQueryService adminQueryService;

    @Autowired
    private AdminMapper adminMapper;

    @Override
    public Result login(AdminDTO adminDTO) {
        Admin admin = adminQueryService.findByAdminName(adminDTO.getAdminName());

        if (admin == null) {
            return Result.error("用户名或密码错误");
        }

        if (!PasswordUtil.matches(adminDTO.getPassword(), admin.getPassword())) {
            return Result.error("用户名或密码错误");
        }

        // 生成访问令牌和刷新令牌
        String accessToken = JwtUtil.generateToken(admin);
        String refreshTokenValue = JwtUtil.generateRefreshToken(admin);
        
        TokenResponse tokenResponse = new TokenResponse(accessToken, refreshTokenValue);

        // 更新最后登录时间
        long currentTime = System.currentTimeMillis();
        admin.setLastLoginTime(currentTime);
        adminMapper.updateLoginTime(admin.getAdminName(), currentTime);

        log.info("管理员 {} 登录成功", admin.getAdminName());
        // 返回包含两个令牌的 TokenResponse 对象
        return Result.success(tokenResponse);
    }
} 