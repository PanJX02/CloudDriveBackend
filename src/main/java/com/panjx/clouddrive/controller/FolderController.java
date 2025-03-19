package com.panjx.clouddrive.controller;

import com.panjx.clouddrive.pojo.FileList;
import com.panjx.clouddrive.pojo.Result;
import com.panjx.clouddrive.service.FolderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
}
