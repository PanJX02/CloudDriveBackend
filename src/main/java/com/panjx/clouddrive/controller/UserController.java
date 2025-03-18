package com.panjx.clouddrive.controller;

import com.panjx.clouddrive.pojo.Result;
import com.panjx.clouddrive.pojo.User;
import com.panjx.clouddrive.pojo.UserDTO;
import com.panjx.clouddrive.service.UserService;
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
                userService.register(userDTO);
                return Result.success();
            }else {
                //用户名已存在
                return Result.error("用户名已存在");
            }
        }
    }

    @GetMapping("/{userId}")
    private Result findById(@PathVariable("userId") Long userId){
        return Result.success();
    }
}
