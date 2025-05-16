package com.panjx.clouddrive.service.admin.impl;

import com.panjx.clouddrive.mapper.AdminMapper;
import com.panjx.clouddrive.pojo.Admin;
import com.panjx.clouddrive.pojo.Result;
import com.panjx.clouddrive.service.admin.AdminDeleteService;
import com.panjx.clouddrive.service.admin.AdminQueryService;
import com.panjx.clouddrive.utils.SecurityUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class AdminDeleteServiceImpl implements AdminDeleteService {

    @Autowired
    private AdminMapper adminMapper;
    
    @Autowired
    private AdminQueryService adminQueryService;
    
    @Override
    public Result deleteAdmin(Long id) {
        // 获取当前登录的管理员ID
        Long currentAdminId = SecurityUtil.getCurrentAdminId();
        if (currentAdminId == null) {
            return Result.error("未获取到管理员信息，请重新登录");
        }
        
        // 判断当前登录的管理员是否为超级管理员(identity=0)
        Admin currentAdmin = adminQueryService.findById(currentAdminId);
        if (currentAdmin == null || currentAdmin.getIdentity() != 0) {
            return Result.error("只有超级管理员才能删除管理员");
        }
        
        // 不能删除自己
        if (currentAdminId.equals(id)) {
            return Result.error("不能删除当前登录的管理员账号");
        }
        
        // 检查要删除的管理员是否存在
        Admin targetAdmin = adminQueryService.findById(id);
        if (targetAdmin == null) {
            return Result.error("要删除的管理员不存在");
        }
        
        // 不能删除超级管理员
        if (targetAdmin.getIdentity() == 0) {
            return Result.error("不能删除超级管理员");
        }
        
        // 执行删除操作
        try {
            int result = adminMapper.deleteAdminById(id);
            if (result > 0) {
                return Result.success("删除管理员成功");
            } else {
                return Result.error("删除管理员失败");
            }
        } catch (Exception e) {
            log.error("删除管理员失败, ID: {}", id, e);
            return Result.error("删除管理员失败：" + e.getMessage());
        }
    }
} 