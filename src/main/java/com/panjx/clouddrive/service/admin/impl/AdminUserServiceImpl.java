package com.panjx.clouddrive.service.admin.impl;

import com.panjx.clouddrive.mapper.UserMapper;
import com.panjx.clouddrive.pojo.Result;
import com.panjx.clouddrive.pojo.User;
import com.panjx.clouddrive.pojo.request.AdminUpdateUserInfoRequest;
import com.panjx.clouddrive.pojo.request.PageRequest;
import com.panjx.clouddrive.service.admin.AdminQueryService;
import com.panjx.clouddrive.service.admin.AdminUserService;
import com.panjx.clouddrive.utils.PasswordUtil;
import com.panjx.clouddrive.utils.SecurityUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
public class AdminUserServiceImpl implements AdminUserService {

    @Autowired
    private UserMapper userMapper;
    
    @Autowired
    private AdminQueryService adminQueryService;

    @Override
    public Result getAllUsers(PageRequest pageRequest) {
        // 检查管理员权限
        Long currentAdminId = SecurityUtil.getCurrentAdminId();
        if (currentAdminId == null) {
            return Result.error("未登录或登录已过期");
        }
        
        // 分页查询（简化实现，不使用PageHelper）
        int page = Math.max(pageRequest.getPage(), 1);
        int pageSize = Math.max(pageRequest.getPageSize(), 10);
        
        // 获取所有用户
        List<User> allUsers = userMapper.findAll();
        
        // 处理敏感信息
        allUsers.forEach(user -> user.setPassword(null));
        
        // 手动实现分页
        int total = allUsers.size();
        int startIndex = (page - 1) * pageSize;
        int endIndex = Math.min(startIndex + pageSize, total);
        
        List<User> pagedUsers = startIndex < total ? 
                allUsers.subList(startIndex, endIndex) : 
                List.of();
        
        // 构建分页结果
        Map<String, Object> pageInfo = new HashMap<>();
        pageInfo.put("list", pagedUsers);
        pageInfo.put("total", total);
        pageInfo.put("pageNum", page);
        pageInfo.put("pageSize", pageSize);
        pageInfo.put("pages", (total + pageSize - 1) / pageSize);
        
        return Result.success(pageInfo);
    }

    @Override
    public Result getUserDetail(Long userId) {
        // 检查管理员权限
        Long currentAdminId = SecurityUtil.getCurrentAdminId();
        if (currentAdminId == null) {
            return Result.error("未登录或登录已过期");
        }
        
        // 查询用户信息
        User user = userMapper.findById(userId);
        if (user == null) {
            return Result.error("用户不存在");
        }
        
        // 处理敏感信息
        user.setPassword(null);
        
        return Result.success(user);
    }

    @Override
    public Result updateUserInfo(AdminUpdateUserInfoRequest request) {
        // 检查管理员权限
        Long currentAdminId = SecurityUtil.getCurrentAdminId();
        if (currentAdminId == null) {
            return Result.error("未登录或登录已过期");
        }
        
        // 验证请求参数
        if (request.getUserId() == null) {
            return Result.error("用户ID不能为空");
        }
        
        // 查询用户是否存在
        User existingUser = userMapper.findById(request.getUserId());
        if (existingUser == null) {
            return Result.error("用户不存在");
        }
        
        // 如果修改用户名，检查用户名是否已被使用
        if (request.getUsername() != null && !request.getUsername().equals(existingUser.getUsername())) {
            User userByUsername = userMapper.findByUsername(request.getUsername());
            if (userByUsername != null) {
                return Result.error("用户名已存在");
            }
        }
        
        // 如果修改邮箱，检查邮箱是否已被使用
        if (request.getEmail() != null && !request.getEmail().equals(existingUser.getEmail())) {
            User userByEmail = userMapper.findByEmail(request.getEmail());
            if (userByEmail != null) {
                return Result.error("邮箱已存在");
            }
        }
        
        // 创建更新对象
        User user = new User();
        user.setUserId(request.getUserId());
        
        // 设置需要更新的字段（除了用户ID外都可更新）
        if (request.getUsername() != null && !request.getUsername().trim().isEmpty()) {
            user.setUsername(request.getUsername());
        }
        
        if (request.getNickname() != null) {
            user.setNickname(request.getNickname());
        }
        
        if (request.getEmail() != null) {
            user.setEmail(request.getEmail());
        }
        
        if (request.getAvatar() != null) {
            user.setAvatar(request.getAvatar());
        }
        
        if (request.getIdentity() != null) {
            user.setIdentity(request.getIdentity());
        }
        
        if (request.getStatus() != null) {
            user.setStatus(request.getStatus());
        }
        
        if (request.getTotalSpace() != null) {
            user.setTotalSpace(request.getTotalSpace());
        }
        
        // 更新用户信息
        userMapper.updateUserInfoByAdmin(user);
        
        // 如果提供了密码，则更新密码
        if (request.getPassword() != null && !request.getPassword().trim().isEmpty()) {
            User passwordUpdate = new User();
            passwordUpdate.setUserId(request.getUserId());
            passwordUpdate.setPassword(PasswordUtil.encode(request.getPassword()));
            userMapper.updatePassword(passwordUpdate);
        }
        
        // 获取更新后的用户信息
        User updatedUser = userMapper.findById(request.getUserId());
        updatedUser.setPassword(null); // 敏感信息处理
        
        return Result.success(updatedUser);
    }
} 