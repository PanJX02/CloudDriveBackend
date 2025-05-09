package com.panjx.clouddrive.service.impl;

import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.panjx.clouddrive.pojo.Admin;
import com.panjx.clouddrive.pojo.Result;
import com.panjx.clouddrive.service.AdminQueryService;
import com.panjx.clouddrive.service.AdminTokenRefreshService;
import com.panjx.clouddrive.utils.JwtUtil;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class AdminTokenRefreshServiceImpl implements AdminTokenRefreshService {

    @Autowired
    private AdminQueryService adminQueryService;

    @Override
    public Result refreshToken(HttpServletRequest request) {
        String authHeader = request.getHeader("Authorization");
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return Result.error("无效的刷新令牌");
        }

        String refreshToken = authHeader.substring(7);

        try {
            if (!JwtUtil.isAdminRefreshToken(refreshToken)) {
                return Result.error("无效的刷新令牌类型");
            }

            DecodedJWT decodedJWT = JwtUtil.verifyToken(refreshToken);
            Long adminId = Long.parseLong(decodedJWT.getSubject());
            Admin admin = adminQueryService.findById(adminId);

            if (admin == null) {
                return Result.error("管理员不存在");
            }

            // 生成新的访问令牌
            String newAccessToken = JwtUtil.generateToken(admin);
            log.info("管理员 {} 刷新令牌成功", admin.getAdminName());
            return Result.success(newAccessToken);

        } catch (JWTVerificationException e) {
            log.warn("管理员刷新令牌失败: {}", e.getMessage());
            return Result.error("刷新令牌无效或已过期");
        } catch (Exception e) {
            log.error("管理员刷新令牌时发生未知错误: {}", e.getMessage(), e);
            return Result.error("刷新令牌失败，请稍后重试");
        }
    }
} 