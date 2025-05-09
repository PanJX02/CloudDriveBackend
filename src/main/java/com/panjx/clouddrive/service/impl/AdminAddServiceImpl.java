package com.panjx.clouddrive.service.impl;

import com.panjx.clouddrive.mapper.AdminMapper;
import com.panjx.clouddrive.pojo.Admin;
import com.panjx.clouddrive.pojo.AdminDTO;
import com.panjx.clouddrive.pojo.Result;
import com.panjx.clouddrive.service.AdminAddService;
import com.panjx.clouddrive.service.AdminQueryService;
import com.panjx.clouddrive.utils.PasswordUtil;
import com.panjx.clouddrive.utils.SecurityUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class AdminAddServiceImpl implements AdminAddService {

    @Autowired
    private AdminMapper adminMapper;

    @Autowired
    private AdminQueryService adminQueryService;

    @Override
    public Result addAdmin(AdminDTO adminDTO) {
        Long currentAdminId = SecurityUtil.getCurrentAdminId();
        if (currentAdminId == null) {
            return Result.error("未登录或登录已过期");
        }

        Admin currentAdmin = adminQueryService.findById(currentAdminId);
        if (currentAdmin == null || currentAdmin.getIdentity() != 0) {
            return Result.error("权限不足，只有超级管理员可以添加管理员");
        }

        Admin existingAdmin = adminQueryService.findByAdminName(adminDTO.getAdminName());
        if (existingAdmin != null) {
            return Result.error("管理员名已存在");
        }

        Admin admin = new Admin();
        admin.setAdminName(adminDTO.getAdminName());
        admin.setPassword(PasswordUtil.encode(adminDTO.getPassword()));
        admin.setNickName(adminDTO.getAdminName());
        admin.setIdentity(1);

        long currentTime = System.currentTimeMillis();
        admin.setRegisterTime(currentTime);
        admin.setLastLoginTime(currentTime);

        try {
            adminMapper.add(admin);
            log.info("添加管理员成功: {}", admin.getAdminName());
            return Result.success("添加管理员成功");
        } catch (Exception e) {
            log.error("添加管理员失败: {}", e.getMessage(), e);
            return Result.error("添加管理员失败，请稍后重试");
        }
    }
} 