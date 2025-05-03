package com.panjx.clouddrive.controller;

import com.panjx.clouddrive.pojo.Result;
import com.panjx.clouddrive.pojo.request.FileIdsRequest;
import com.panjx.clouddrive.service.file.RecycleBinService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * 回收站控制器
 */
@Slf4j
@RestController
@RequestMapping("/recycle-bin")
public class RecycleBinController {

    @Autowired
    private RecycleBinService recycleBinService;
    
    /**
     * 获取回收站文件列表
     * @return 回收站文件列表
     */
    @GetMapping
    public Result getRecycleBinFiles() {
        log.info("获取回收站文件列表");
        return recycleBinService.getRecycleBinFiles();
    }
    
    /**
     * 恢复回收站中的文件或文件夹
     * @param fileIdsRequest 请求对象，包含文件/文件夹ID列表
     * @return 操作结果
     */
    @PostMapping("/restore")
    public Result restoreFiles(@RequestBody FileIdsRequest fileIdsRequest) {
        log.info("恢复回收站文件");
        log.info("文件ID列表：{}", fileIdsRequest.getIds());
        return recycleBinService.restoreFiles(fileIdsRequest.getIds());
    }
    
    /**
     * 永久删除回收站中的文件或文件夹
     * @param fileIdsRequest 请求对象，包含文件/文件夹ID列表
     * @return 操作结果
     */
    @PostMapping("/delete")
    public Result deleteFilesForever(@RequestBody FileIdsRequest fileIdsRequest) {
        log.info("永久删除回收站文件");
        log.info("文件ID列表：{}", fileIdsRequest.getIds());
        return recycleBinService.deleteFilesForever(fileIdsRequest.getIds());
    }
    
    /**
     * 清空回收站
     * @return 操作结果
     */
    @PostMapping("/clear")
    public Result clearRecycleBin() {
        log.info("清空回收站");
        return recycleBinService.clearRecycleBin();
    }
} 