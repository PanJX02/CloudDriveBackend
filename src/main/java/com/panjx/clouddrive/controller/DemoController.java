package com.panjx.clouddrive.controller;

import com.panjx.clouddrive.utils.SecurityUtil;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/demo")
public class DemoController {

    @GetMapping("/user-info")
    public Map<String, Object> getUserInfo() {
        Map<String, Object> result = new HashMap<>();
        
        // 获取当前用户信息
        String username = SecurityUtil.getCurrentUsername();
        Long userId = SecurityUtil.getCurrentUserId();
        
        result.put("username", username);
        result.put("userId", userId);
        result.put("authenticated", SecurityUtil.isAuthenticated());
        
        return result;
    }
} 