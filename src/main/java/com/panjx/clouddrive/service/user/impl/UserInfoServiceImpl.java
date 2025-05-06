package com.panjx.clouddrive.service.user.impl;

import com.panjx.clouddrive.mapper.UserMapper;
import com.panjx.clouddrive.pojo.Result;
import com.panjx.clouddrive.pojo.User;
import com.panjx.clouddrive.pojo.request.UpdatePasswordRequest;
import com.panjx.clouddrive.pojo.request.UpdateUserInfoRequest;
import com.panjx.clouddrive.service.user.UserInfoService;
import com.panjx.clouddrive.utils.PasswordUtil;
import com.panjx.clouddrive.utils.SecurityUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserInfoServiceImpl implements UserInfoService {

    @Autowired
    private UserMapper userMapper;

    @Override
    public User findByUsername(String username) {
        return userMapper.findByUsername(username);
    }

    @Override
    public User findById(Long userId) {
        return userMapper.findById(userId);
    }

    @Override
    public Result getCurrentUserInfo() {
        // 从安全上下文获取当前用户ID
        Long userId = SecurityUtil.getCurrentUserId();
        if (userId == null) {
            return Result.error("用户未登录");
        }
        
        // 查询用户信息
        User user = findById(userId);
        if (user != null) {
            // 敏感信息处理，不返回密码
            user.setPassword(null);
            return Result.success(user);
        } else {
            return Result.error("用户不存在");
        }
    }

    @Override
    public Result updateUserInfo(UpdateUserInfoRequest request) {
        // 从安全上下文获取当前用户ID
        Long userId = SecurityUtil.getCurrentUserId();
        if (userId == null) {
            return Result.error("用户未登录");
        }
        
        // 查询用户信息确认用户存在
        User existingUser = findById(userId);
        if (existingUser == null) {
            return Result.error("用户不存在");
        }
        
        // 如果邮箱有变更，检查邮箱是否已被使用
        if (request.getEmail() != null && !request.getEmail().equals(existingUser.getEmail())) {
            // 这里需要添加检查邮箱是否已被使用的逻辑
            // 简化处理，直接更新
        }
        
        // 创建更新对象，只设置非空字段
        User user = new User();
        user.setUserId(userId);
        
        // 只更新非空字段，保留原有值
        if (request.getNickname() != null && !request.getNickname().trim().isEmpty()) {
            user.setNickname(request.getNickname());
        }
        
        if (request.getEmail() != null && !request.getEmail().trim().isEmpty()) {
            user.setEmail(request.getEmail());
        }
        
        if (request.getAvatar() != null && !request.getAvatar().trim().isEmpty()) {
            user.setAvatar(request.getAvatar());
        }
        
        userMapper.updateUserInfo(user);
        
        // 获取更新后的用户信息
        User updatedUser = findById(userId);
        updatedUser.setPassword(null);  // 敏感信息处理
        
        return Result.success(updatedUser);
    }
    
    @Override
    public Result updatePassword(UpdatePasswordRequest request) {
        // 从安全上下文获取当前用户ID
        Long userId = SecurityUtil.getCurrentUserId();
        if (userId == null) {
            return Result.error("用户未登录");
        }
        
        // 查询用户信息确认用户存在
        User existingUser = findById(userId);
        if (existingUser == null) {
            return Result.error("用户不存在");
        }
        
        // 验证原密码是否正确
        if (!PasswordUtil.matches(request.getOldPassword(), existingUser.getPassword())) {
            return Result.error("原密码不正确");
        }
        
        // 验证新密码是否与原密码相同
        if (request.getOldPassword().equals(request.getNewPassword())) {
            return Result.error("新密码不能与原密码相同");
        }
        
        // 加密新密码
        String encryptedNewPassword = PasswordUtil.encode(request.getNewPassword());
        
        // 更新密码
        User user = new User();
        user.setUserId(userId);
        user.setPassword(encryptedNewPassword);
        userMapper.updatePassword(user);
        
        return Result.success("密码修改成功");
    }
} 