package com.panjx.clouddrive.controller;

import com.panjx.clouddrive.pojo.AdminDTO;
import com.panjx.clouddrive.pojo.Result;
import com.panjx.clouddrive.pojo.request.PageRequest;
import com.panjx.clouddrive.service.AdminAddService;
import com.panjx.clouddrive.service.AdminFileService;
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
    private AdminAddService adminAddService;
    
    @Autowired
    private AdminFileService adminFileService;
    
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
        return adminAddService.addAdmin(adminDTO);
    }
    
    /**
     * 获取所有文件信息（分页）
     */
    @GetMapping("/files")
    public Result getAllFiles(PageRequest pageRequest) {
        log.info("管理员获取所有文件信息，页码：{}，每页数量：{}", pageRequest.getPage(), pageRequest.getPageSize());
        return adminFileService.getAllFiles(pageRequest);
    }
} 