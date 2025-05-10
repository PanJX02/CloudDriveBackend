package com.panjx.clouddrive.controller;

import com.panjx.clouddrive.pojo.AdminDTO;
import com.panjx.clouddrive.pojo.Result;
import com.panjx.clouddrive.pojo.request.AdminUpdateUserInfoRequest;
import com.panjx.clouddrive.pojo.request.PageRequest;
import com.panjx.clouddrive.pojo.request.UpdateAdminRequest;
import com.panjx.clouddrive.pojo.request.UpdateFileRequest;
import com.panjx.clouddrive.pojo.request.UpdateShareRequest;
import com.panjx.clouddrive.service.admin.AdminService;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/admins")
public class AdminController {

    @Autowired
    private AdminService adminService;
    
    /**
     * 添加管理员（仅超级管理员可操作）
     */
    @PostMapping
    public Result addAdmin(@Valid @RequestBody AdminDTO adminDTO, BindingResult bindingResult) {
        // 检查验证结果
        log.info("添加管理员");
        if (bindingResult.hasErrors()) {
            // 获取第一个错误信息
            String errorMessage = bindingResult.getAllErrors().getFirst().getDefaultMessage();
            return Result.error(errorMessage);
        }
        
        // 添加管理员
        return adminService.addAdmin(adminDTO);
    }
    
    /**
     * 获取所有文件信息（分页）
     */
    @GetMapping("/files")
    public Result getAllFiles(PageRequest pageRequest) {
        log.info("管理员获取所有文件信息，页码：{}，每页数量：{}", pageRequest.getPage(), pageRequest.getPageSize());
        return adminService.getAllFiles(pageRequest);
    }
    
    /**
     * 修改文件信息
     */
    @PutMapping("/files")
    public Result updateFileInfo(@RequestBody UpdateFileRequest updateFileRequest) {
        log.info("管理员修改文件信息，文件ID：{}", updateFileRequest.getFileId());
        if (updateFileRequest.getFileId() == null) {
            return Result.error("文件ID不能为空");
        }
        return adminService.updateFileInfo(updateFileRequest);
    }

    /**
     * 获取所有分享信息
     *
     * @param pageRequest 分页请求参数
     * @return 分页后的分享列表
     */
    @GetMapping("/shares")
    public Result getAllShares(PageRequest pageRequest) {
        log.info("管理员获取所有分享信息，页码：{}，每页数量：{}", pageRequest.getPage(), pageRequest.getPageSize());
        return adminService.getAllShares(pageRequest);
    }

    /**
     * 修改分享信息
     *
     * @param updateShareRequest 更新分享请求参数
     * @return 修改结果
     */
    @PutMapping("/shares")
    public Result updateShare(@RequestBody UpdateShareRequest updateShareRequest) {
        log.info("管理员修改分享信息，分享ID：{}", updateShareRequest.getShareId());
        return adminService.updateShare(updateShareRequest);
    }
    
    /**
     * 获取所有用户信息（分页）
     */
    @GetMapping("/users")
    public Result getAllUsers(PageRequest pageRequest) {
        log.info("管理员获取所有用户信息，页码：{}，每页数量：{}", pageRequest.getPage(), pageRequest.getPageSize());
        return adminService.getAllUsers(pageRequest);
    }
    
    /**
     * 获取用户详情
     */
    @GetMapping("/users/{userId}")
    public Result getUserDetail(@PathVariable("userId") Long userId) {
        log.info("管理员获取用户详情，用户ID：{}", userId);
        return adminService.getUserDetail(userId);
    }
    
    /**
     * 修改用户信息
     */
    @PutMapping("/users")
    public Result updateUserInfo(@RequestBody AdminUpdateUserInfoRequest request) {
        log.info("管理员修改用户信息，用户ID：{}", request.getUserId());
        return adminService.updateUserInfo(request);
    }
    
    /**
     * 获取所有管理员信息（分页）
     */
    @GetMapping
    public Result getAllAdmins(PageRequest pageRequest) {
        log.info("获取所有管理员信息，页码：{}，每页数量：{}", pageRequest.getPage(), pageRequest.getPageSize());
        return adminService.getAllAdmins(pageRequest);
    }
    
    /**
     * 修改管理员信息
     */
    @PutMapping
    public Result updateAdmin(@RequestBody UpdateAdminRequest request) {
        log.info("修改管理员信息，管理员ID：{}", request.getId());
        if (request.getId() == null) {
            return Result.error("管理员ID不能为空");
        }
        return adminService.updateAdmin(request);
    }
} 