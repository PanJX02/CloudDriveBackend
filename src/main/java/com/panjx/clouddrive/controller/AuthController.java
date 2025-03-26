package com.panjx.clouddrive.controller;

import com.panjx.clouddrive.pojo.Result;
import com.panjx.clouddrive.pojo.User;
import com.panjx.clouddrive.pojo.UserDTO;
import com.panjx.clouddrive.service.AuthService;
import com.panjx.clouddrive.service.UserService;
import com.panjx.clouddrive.utils.PasswordUtil;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping ("/auth")
public class AuthController {


    @Autowired
    private AuthService authService;
    @PostMapping("/tokens")
    public Result login(@Valid @RequestBody UserDTO userDTO, BindingResult bindingResult)
    {
        // 检查验证结果
        log.info("获取token");
        if (bindingResult.hasErrors()) {
            // 获取第一个错误信息
            String errorMessage = bindingResult.getAllErrors().getFirst().getDefaultMessage();
            return Result.error(errorMessage);
        }

        // 登录
        return authService.authenticate(userDTO);

    }
    
    @PostMapping("/refresh")
    public Result refreshToken(@RequestBody Map<String, String> requestBody) {
        String refreshToken = requestBody.get("refreshToken");
        
        if (refreshToken == null || refreshToken.isEmpty()) {
            return Result.error("刷新令牌不能为空");
        }
        
        log.info("刷新令牌");
        return authService.refreshToken(refreshToken);
    }
}
