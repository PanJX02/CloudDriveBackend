package com.panjx.clouddrive.controller;

import com.panjx.clouddrive.pojo.Result;
import com.panjx.clouddrive.pojo.response.TokenResponse;
import com.panjx.clouddrive.pojo.User;
import com.panjx.clouddrive.pojo.UserDTO;
import com.panjx.clouddrive.service.UserService;
import com.panjx.clouddrive.utils.SecurityUtil;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping ("/users")
public class UserController {

    @Autowired
    private UserService userService;
    @PostMapping
    public Result register(@Valid @RequestBody UserDTO userDTO, BindingResult bindingResult){
        // 检查验证结果
        if (bindingResult.hasErrors()) {
            // 获取第一个错误信息
            String errorMessage = bindingResult.getAllErrors().getFirst().getDefaultMessage();
            return Result.error(errorMessage);
        }else{
            //查询是否有该用户
            User u = userService.findByUsername(userDTO.getUsername());
            if (u==null){
                //注册
                TokenResponse tokenResponse = userService.register(userDTO);
                return Result.success(tokenResponse);
            }else {
                //用户名已存在
                return Result.error("用户名已存在");
            }
        }
    }

    /**
     * 根据用户ID获取用户信息（用于管理员查询特定用户）
     */
    @GetMapping("/{userId}")
    public Result findById(@PathVariable("userId") Long userId){
        // 根据ID查询用户
        User user = userService.findById(userId);
        if (user != null) {
            // 敏感信息处理，不返回密码
            user.setPassword(null);
            return Result.success(user);
        } else {
            return Result.error("用户不存在");
        }
    }
    
    /**
     * 获取当前登录用户信息
     */
    @GetMapping("/current")
    public Result getCurrentUser(){
        // 从安全上下文获取当前用户ID
        Long userId = SecurityUtil.getCurrentUserId();
        if (userId == null) {
            return Result.error("用户未登录");
        }
        
        // 查询用户信息
        User user = userService.findById(userId);
        if (user != null) {
            // 敏感信息处理，不返回密码
            user.setPassword(null);
            return Result.success(user);
        } else {
            return Result.error("用户不存在");
        }
    }
}
