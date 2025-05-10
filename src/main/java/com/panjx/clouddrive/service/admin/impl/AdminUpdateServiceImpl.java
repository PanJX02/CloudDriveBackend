package com.panjx.clouddrive.service.admin.impl;

import com.panjx.clouddrive.mapper.AdminMapper;
import com.panjx.clouddrive.pojo.Admin;
import com.panjx.clouddrive.pojo.Result;
import com.panjx.clouddrive.pojo.request.UpdateAdminRequest;
import com.panjx.clouddrive.service.admin.AdminQueryService;
import com.panjx.clouddrive.service.admin.AdminUpdateService;
import com.panjx.clouddrive.utils.PasswordUtil;
import com.panjx.clouddrive.utils.SecurityUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class AdminUpdateServiceImpl implements AdminUpdateService {

    @Autowired
    private AdminMapper adminMapper;
    
    @Autowired
    private AdminQueryService adminQueryService;

    @Override
    public Result updateAdmin(UpdateAdminRequest request) {
        // 检查当前管理员权限
        Long currentAdminId = SecurityUtil.getCurrentAdminId();
        if (currentAdminId == null) {
            return Result.error("未登录或登录已过期");
        }
        
        // 查询当前管理员信息
        Admin currentAdmin = adminQueryService.findById(currentAdminId);
        if (currentAdmin == null) {
            return Result.error("管理员信息异常");
        }
        
        // 检查是否为超级管理员(identity=0)
        if (currentAdmin.getIdentity() != 0) {
            return Result.error("权限不足，只有超级管理员才能修改管理员信息");
        }
        
        // 验证管理员是否存在
        Admin existingAdmin = adminQueryService.findById(request.getId());
        if (existingAdmin == null) {
            return Result.error("管理员不存在");
        }
        
        // 查询管理员名是否被其他管理员使用
        if (request.getAdminName() != null && !request.getAdminName().equals(existingAdmin.getAdminName())) {
            Admin adminWithSameName = adminQueryService.findByAdminName(request.getAdminName());
            if (adminWithSameName != null) {
                return Result.error("管理员名已被使用");
            }
        }
        
        // 准备更新的管理员对象
        Admin admin = new Admin();
        admin.setId(request.getId());
        admin.setAdminName(request.getAdminName());
        
        // 如果更新密码，需要加密
        if (request.getPassword() != null && !request.getPassword().isEmpty()) {
            admin.setPassword(PasswordUtil.encode(request.getPassword()));
        }
        
        admin.setNickName(request.getNickName());
        admin.setIdentity(request.getIdentity());
        
        // 更新管理员信息
        adminMapper.updateAdmin(admin);
        
        return Result.success("更新管理员信息成功");
    }
} 