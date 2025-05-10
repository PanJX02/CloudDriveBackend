package com.panjx.clouddrive.service.admin.impl;

import com.panjx.clouddrive.pojo.Admin;
import com.panjx.clouddrive.pojo.AdminDTO;
import com.panjx.clouddrive.pojo.Result;
import com.panjx.clouddrive.pojo.request.AdminUpdateUserInfoRequest;
import com.panjx.clouddrive.pojo.request.PageRequest;
import com.panjx.clouddrive.pojo.request.UpdateFileRequest;
import com.panjx.clouddrive.pojo.request.UpdateShareRequest;
import com.panjx.clouddrive.service.admin.*;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class AdminServiceImpl implements AdminService {

    @Autowired
    private AdminLoginService adminLoginService;
    
    @Autowired
    private AdminAddService adminAddService;
    
    @Autowired
    private AdminQueryService adminQueryService;
    
    @Autowired
    private AdminShareService adminShareService;
    
    @Autowired
    private AdminFileService adminFileService;

    @Autowired
    private AdminUserService adminUserService;

    @Autowired
    private AdminTokenRefreshService adminTokenRefreshService;
    
    @Autowired
    private AdminListService adminListService;

    @Override
    public Result login(AdminDTO adminDTO) {
        return adminLoginService.login(adminDTO);
    }

    @Override
    public Result addAdmin(AdminDTO adminDTO) {
        return adminAddService.addAdmin(adminDTO);
    }

    @Override
    public Admin findByAdminName(String adminName) {
        return adminQueryService.findByAdminName(adminName);
    }

    @Override
    public Admin findById(Long id) {
        return adminQueryService.findById(id);
    }

    @Override
    public Result getAllShares(PageRequest pageRequest) {
        return adminShareService.getAllShares(pageRequest);
    }

    @Override
    public Result updateShare(UpdateShareRequest updateShareRequest) {
        return adminShareService.updateShare(updateShareRequest);
    }

    @Override
    public Result getAllFiles(PageRequest pageRequest) {
        return adminFileService.getAllFiles(pageRequest);
    }

    @Override
    public Result updateFileInfo(UpdateFileRequest updateFileRequest) {
        return adminFileService.updateFileInfo(updateFileRequest);
    }

    @Override
    public Result refreshToken(HttpServletRequest request) {
        return adminTokenRefreshService.refreshToken(request);
    }
    
    @Override
    public Result getAllUsers(PageRequest pageRequest) {
        return adminUserService.getAllUsers(pageRequest);
    }
    
    @Override
    public Result getUserDetail(Long userId) {
        return adminUserService.getUserDetail(userId);
    }
    
    @Override
    public Result updateUserInfo(AdminUpdateUserInfoRequest request) {
        return adminUserService.updateUserInfo(request);
    }
    
    @Override
    public Result getAllAdmins(PageRequest pageRequest) {
        return adminListService.getAllAdmins(pageRequest);
    }
} 