package com.panjx.clouddrive.controller;

import com.panjx.clouddrive.pojo.AdminDTO;
import com.panjx.clouddrive.pojo.Result;
import com.panjx.clouddrive.service.AdminLoginService;
import com.panjx.clouddrive.service.AdminTokenRefreshService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/admin/auth")
public class AdminAuthController {

    @Autowired
    private AdminLoginService adminLoginService;

    @Autowired
    private AdminTokenRefreshService adminTokenRefreshService;
    
    /**
     * 管理员登录
     */
    @PostMapping("/login")
    public Result login(@Valid @RequestBody AdminDTO adminDTO, BindingResult bindingResult) {
        log.info("管理员登录");
        // 检查验证结果
        if (bindingResult.hasErrors()) {
            // 获取第一个错误信息
            String errorMessage = bindingResult.getAllErrors().getFirst().getDefaultMessage();
            return Result.error(errorMessage);
        }
        return adminLoginService.login(adminDTO);
    }
    
    /**
     * 刷新管理员令牌
     */
    @PostMapping("/refresh")
    public Result refreshToken(HttpServletRequest request) {
        log.info("刷新管理员令牌");
        return adminTokenRefreshService.refreshToken(request);
    }
} 