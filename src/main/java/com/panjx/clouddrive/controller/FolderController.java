package com.panjx.clouddrive.controller;

import com.panjx.clouddrive.pojo.response.FileList;
import com.panjx.clouddrive.pojo.Result;
import com.panjx.clouddrive.pojo.UserFile;
import com.panjx.clouddrive.pojo.request.CreateFolderRequest;
import com.panjx.clouddrive.service.folder.FolderService;
import com.panjx.clouddrive.utils.SecurityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/folders")
public class FolderController {

    @Autowired
    private FolderService folderService;

    @GetMapping("/{folderId}/files")
    public Result getFiles(@PathVariable String folderId) {
        FileList fileList = folderService.getFiles(Long.valueOf(folderId));
        if (fileList != null) {
            return Result.success(fileList);
        }
        return Result.error("获取文件列表失败");
    }
    
    /**
     * 创建文件夹
     * @param request 请求对象，包含文件夹名称和父目录ID
     * @return 创建结果
     */
    @PostMapping("/create")
    public Result createFolder(@RequestBody CreateFolderRequest request) {
        // 参数校验
        if (request.getFolderName() == null || request.getFolderName().trim().isEmpty()) {
            return Result.error("文件夹名称不能为空");
        }
        
        // 获取当前用户ID
        Long userId = SecurityUtils.getCurrentUserId();
        if (userId == null) {
            return Result.error("用户未登录");
        }
        
        // 创建文件夹
        Long parentId = request.getParentId() != null ? request.getParentId() : 0L;

        return folderService.createFolder(userId, request.getFolderName(), parentId);

    }
}
