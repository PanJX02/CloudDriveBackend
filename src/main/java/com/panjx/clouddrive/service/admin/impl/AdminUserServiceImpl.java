package com.panjx.clouddrive.service.admin.impl;

import com.panjx.clouddrive.mapper.UserMapper;
import com.panjx.clouddrive.pojo.Result;
import com.panjx.clouddrive.pojo.User;
import com.panjx.clouddrive.pojo.request.AdminUpdateUserInfoRequest;
import com.panjx.clouddrive.pojo.request.PageRequest;
import com.panjx.clouddrive.pojo.response.PageMeta;
import com.panjx.clouddrive.pojo.response.UserList;
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
        
        // 分页查询
        int page = Math.max(pageRequest.getPage(), 1);
        int pageSize = Math.max(pageRequest.getPageSize(), 10);
        
        // 获取总记录数
        int total = userMapper.countAllUsers();
        
        // 计算总页数
        int totalPage = (total + pageSize - 1) / pageSize;
        
        // 计算分页的offset
        int offset = (page - 1) * pageSize;
        
        // 查询分页数据
        List<User> users = userMapper.getUsersByPage(offset, pageSize);
        
        // 处理敏感信息
        users.forEach(user -> user.setPassword(null));
        
        // 封装分页元数据
        PageMeta pageMeta = new PageMeta(total, totalPage, pageSize, page);
        
        // 封装结果
        UserList userList = new UserList(users, pageMeta);
        
        return Result.success(userList);
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