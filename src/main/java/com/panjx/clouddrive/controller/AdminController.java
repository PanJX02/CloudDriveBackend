package com.panjx.clouddrive.controller;

import com.panjx.clouddrive.pojo.AdminDTO;
import com.panjx.clouddrive.pojo.Result;
import com.panjx.clouddrive.pojo.request.PageRequest;
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
        if (updateShareRequest.getShareId() == null) {
            return Result.error("分享ID不能为空");
        }
        return adminService.updateShare(updateShareRequest);
    }
} 