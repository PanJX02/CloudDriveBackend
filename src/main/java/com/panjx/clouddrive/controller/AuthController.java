package com.panjx.clouddrive.controller;

import com.panjx.clouddrive.pojo.Result;
import com.panjx.clouddrive.pojo.User;
import com.panjx.clouddrive.pojo.UserDTO;
import com.panjx.clouddrive.service.AuthService;
import com.panjx.clouddrive.service.UserService;
import com.panjx.clouddrive.utils.PasswordUtil;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping ("/auth")
public class AuthController {


    @Autowired
    private AuthService authService;
    @PostMapping("/tokens")
    public Result login(@Valid @RequestBody UserDTO userDTO, BindingResult bindingResult)
    {
        // 检查验证结果
        if (bindingResult.hasErrors()) {
            // 获取第一个错误信息
            String errorMessage = bindingResult.getAllErrors().getFirst().getDefaultMessage();
            return Result.error(errorMessage);
        }

        // 登录
        return authService.authenticate(userDTO);

    }
}
