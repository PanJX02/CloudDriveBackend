package com.panjx.clouddrive.controller;

import com.panjx.clouddrive.pojo.Result;
import com.panjx.clouddrive.pojo.User;
import com.panjx.clouddrive.pojo.UserDTO;
import com.panjx.clouddrive.pojo.request.UpdatePasswordRequest;
import com.panjx.clouddrive.pojo.request.UpdateUserInfoRequest;
import com.panjx.clouddrive.service.UserService;
import com.panjx.clouddrive.utils.SecurityUtil;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

/**
 * 用户相关接口控制器
 */
@RestController
@RequestMapping("/users")
public class UserController {

    @Autowired
    private UserService userService;
    
    /**
     * 用户注册
     */
    @PostMapping
    public Result register(@Valid @RequestBody UserDTO userDTO, BindingResult bindingResult){
        // 检查验证结果
        if (bindingResult.hasErrors()) {
            // 获取第一个错误信息
            String errorMessage = bindingResult.getAllErrors().getFirst().getDefaultMessage();
            return Result.error(errorMessage);
        } else {
            // 直接调用服务层处理注册（包括用户名检查）
            return userService.register(userDTO);
        }
    }

    /**
     * 根据用户ID获取用户信息（用于管理员查询特定用户）
     */
    @GetMapping("/{userId}")
    public Result findUserById(@PathVariable("userId") Long userId){
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
    public Result getCurrentUserInfo(){
        // 直接调用服务层方法
        return userService.getCurrentUserInfo();
    }
    
    /**
     * 更新当前登录用户信息
     */
    @PutMapping("/current")
    public Result updateUserInfo(@RequestBody UpdateUserInfoRequest request) {
        return userService.updateUserInfo(request);
    }
    
    /**
     * 修改当前登录用户密码
     */
    @PutMapping("/password")
    public Result updatePassword(@Valid @RequestBody UpdatePasswordRequest request, BindingResult bindingResult) {
        // 检查验证结果
        if (bindingResult.hasErrors()) {
            // 获取第一个错误信息
            String errorMessage = bindingResult.getAllErrors().getFirst().getDefaultMessage();
            return Result.error(errorMessage);
        }
        
        return userService.updatePassword(request);
    }
}
