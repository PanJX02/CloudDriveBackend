package com.panjx.clouddrive.controller;

import com.panjx.clouddrive.pojo.Result;
import com.panjx.clouddrive.pojo.request.CopyFileRequest;
import com.panjx.clouddrive.pojo.request.FileIdsRequest;
import com.panjx.clouddrive.pojo.request.FileSearchRequest;
import com.panjx.clouddrive.pojo.request.MoveFileRequest;
import com.panjx.clouddrive.pojo.request.RenameFileRequest;
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
        log.info("文件ID列表：{} 目标文件夹ID：{}", moveFileRequest.getIds(), moveFileRequest.getTargetFolderId());
        return fileService.moveFile(moveFileRequest);
    }

    @PostMapping("/copy")
    public Result copyFile(@RequestBody CopyFileRequest copyFileRequest) {
        log.info("复制文件/文件夹");
        log.info("文件ID列表：{} 目标文件夹ID：{}", copyFileRequest.getIds(), copyFileRequest.getTargetFolderId());
        return fileService.copyFile(copyFileRequest);
    }

    @PostMapping("/favorite")
    public Result favoriteFiles(@RequestBody FileIdsRequest fileIdsRequest) {
        log.info("收藏文件/文件夹");
        log.info("文件ID列表：{}", fileIdsRequest.getIds());
        return fileService.favoriteFiles(fileIdsRequest.getIds());
    }

    @PostMapping("/unfavorites")
    public Result unfavoriteFiles(@RequestBody FileIdsRequest fileIdsRequest) {
        log.info("取消收藏文件/文件夹");
        log.info("文件ID列表：{}", fileIdsRequest.getIds());
        return fileService.unfavoriteFiles(fileIdsRequest.getIds());
    }

    @GetMapping("/favorites")
    public Result getFavoriteFiles() {
        log.info("获取收藏文件列表");
        return fileService.getFavoriteFiles();
    }

    /**
     * 获取文件或文件夹的详细信息
     * @param fileIdsRequest 请求对象，包含文件ID列表
     * @return 详细信息结果
     */
    @PostMapping("/detail")
    public Result getFileDetails(@RequestBody FileIdsRequest fileIdsRequest) {
        log.info("获取文件/文件夹详情");
        log.info("文件ID列表：{}", fileIdsRequest.getIds());
        return fileService.getFileDetails(fileIdsRequest.getIds());
    }
    
    /**
     * 删除文件或文件夹
     * @param fileIdsRequest 请求对象，包含文件/文件夹ID列表
     * @return 删除结果
     */
    @PostMapping("/delete")
    public Result deleteFiles(@RequestBody FileIdsRequest fileIdsRequest) {
        log.info("删除文件/文件夹");
        log.info("文件ID列表：{}", fileIdsRequest.getIds());
        return fileService.deleteFiles(fileIdsRequest.getIds());
    }
    
    /**
     * 搜索文件
     * @param searchRequest 搜索请求参数
     * @return 搜索结果
     */
    @PostMapping("/search")
    public Result searchFiles(@RequestBody FileSearchRequest searchRequest) {
        log.info("搜索文件");
        log.info("搜索关键词：{} 文件夹ID：{}", 
                searchRequest.getKeyword(), 
                searchRequest.getFolderId());
        return fileService.searchFiles(searchRequest);
    }

    /**
     * 重命名文件或文件夹
     */
    @PostMapping("/rename")
    public Result renameFile(@RequestBody RenameFileRequest renameFileRequest) {
        log.info("重命名文件/文件夹");
        log.info("文件ID：{} 新文件名：{}", renameFileRequest.getId(), renameFileRequest.getNewFileName());
        return fileService.renameFile(renameFileRequest);
    }
}
