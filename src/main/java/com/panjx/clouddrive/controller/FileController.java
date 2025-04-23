package com.panjx.clouddrive.controller;

import com.panjx.clouddrive.pojo.Result;
import com.panjx.clouddrive.pojo.request.CopyFileRequest;
import com.panjx.clouddrive.pojo.request.FileIdRequest;
import com.panjx.clouddrive.pojo.request.MoveFileRequest;
import com.panjx.clouddrive.pojo.request.UploadRequest;
import com.panjx.clouddrive.pojo.UserFile;
import com.panjx.clouddrive.service.file.FileService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/files")
public class FileController {

    @Autowired
    private FileService fileService;

    @PostMapping("/upload")
    public Result upload(@RequestBody UploadRequest uploadRequest) {
        log.info("上传文件");
        log.info("文件名：{}  扩展名：{}  SHA256：{}  pid：{}", uploadRequest.getFileName(),uploadRequest.getFileExtension(),uploadRequest.getFileSHA256(),uploadRequest.getFilePid());
        return fileService.upload(uploadRequest.getFileName(),uploadRequest.getFileExtension(),uploadRequest.getFileSHA256(),uploadRequest.getFilePid());
    }
    
    @PostMapping("/upload/complete")
    public Result uploadComplete(@RequestBody UserFile userFile) {
        log.info("接收上传完成通知");
        log.info("文件名：{} 大小：{} SHA256：{}", userFile.getFileName(), userFile.getFileSize(), userFile.getFileSHA256());
        return fileService.uploadComplete(userFile);
    }

    @PostMapping("/download")
    public Result download(@RequestBody UserFile userFile) {
        log.info("下载文件");
        log.info("文件ID：{}", userFile.getId());
        return fileService.download(userFile);
    }
    
    @PostMapping("/move")
    public Result moveFile(@RequestBody MoveFileRequest moveFileRequest) {
        log.info("移动文件/文件夹");
        log.info("文件ID：{} 目标文件夹ID：{}", moveFileRequest.getId(), moveFileRequest.getTargetFolderId());
        return fileService.moveFile(moveFileRequest);
    }
    
    @PostMapping("/copy")
    public Result copyFile(@RequestBody CopyFileRequest copyFileRequest) {
        log.info("复制文件/文件夹");
        log.info("文件ID：{} 目标文件夹ID：{}", copyFileRequest.getId(), copyFileRequest.getTargetFolderId());
        return fileService.copyFile(copyFileRequest);
    }
    
    @PostMapping("/favorite")
    public Result favoriteFile(@RequestBody FileIdRequest fileIdRequest) {
        log.info("收藏文件/文件夹");
        log.info("文件ID：{}", fileIdRequest.getId());
        return fileService.favoriteFile(fileIdRequest.getId());
    }
    
    @PostMapping("/unfavorite")
    public Result unfavoriteFile(@RequestBody FileIdRequest fileIdRequest) {
        log.info("取消收藏文件/文件夹");
        log.info("文件ID：{}", fileIdRequest.getId());
        return fileService.unfavoriteFile(fileIdRequest.getId());
    }
    
    @GetMapping("/favorites")
    public Result getFavoriteFiles() {
        log.info("获取收藏文件列表");
        return fileService.getFavoriteFiles();
    }
}
