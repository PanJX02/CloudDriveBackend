package com.panjx.clouddrive.service.file.impl;

import com.panjx.clouddrive.mapper.FileMapper;
import com.panjx.clouddrive.pojo.Result;
import com.panjx.clouddrive.pojo.UserFile;
import com.panjx.clouddrive.pojo.request.CopyFileRequest;
import com.panjx.clouddrive.pojo.request.MoveFileRequest;
import com.panjx.clouddrive.service.file.FileCopyService;
import com.panjx.clouddrive.service.file.FileDownloadService;
import com.panjx.clouddrive.service.file.FileFavoriteService;
import com.panjx.clouddrive.service.file.FileMoveService;
import com.panjx.clouddrive.service.file.FileService;
import com.panjx.clouddrive.service.file.FileUploadService;
import com.panjx.clouddrive.utils.SecurityUtils;
import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
public class FileServiceImpl implements FileService {

    @Autowired
    private FileUploadService fileUploadService;

    @Autowired
    private FileDownloadService fileDownloadService;

    @Autowired
    private FileMoveService fileMoveService;
    
    @Autowired
    private FileCopyService fileCopyService;
    
    @Autowired
    private FileFavoriteService fileFavoriteService;
    
    @Autowired
    private FileMapper fileMapper;

    @Override
    public Result upload(String fileName,String fileExtension, String fileSHA256, Long file_pid) {
        return fileUploadService.upload(fileName,fileExtension,fileSHA256,file_pid);
    }

    @Override
    public Result uploadComplete(UserFile userFile) {
        return fileUploadService.uploadComplete(userFile);
    }

    /**
     * 处理文件下载的操作
     * @param userFile 用户文件对象
     * @return 下载结果
     */
    @Override
    public Result download(UserFile userFile) {
        return fileDownloadService.download(userFile);
    }

    /**
     * 处理文件移动的操作
     * @param moveFileRequest 文件移动请求
     * @return 移动结果
     */
    @Override
    public Result moveFile(MoveFileRequest moveFileRequest) {
        return fileMoveService.moveFile(moveFileRequest);
    }
    
    /**
     * 处理文件复制的操作
     * @param copyFileRequest 文件复制请求
     * @return 复制结果
     */
    @Override
    public Result copyFile(CopyFileRequest copyFileRequest) {
        return fileCopyService.copyFile(copyFileRequest);
    }
    
    /**
     * 收藏文件
     * @param userFileId 用户文件ID
     * @return 操作结果
     */
    @Override
    public Result favoriteFile(Long userFileId) {
        return fileFavoriteService.favoriteFile(userFileId);
    }
    
    /**
     * 取消收藏文件
     * @param userFileId 用户文件ID
     * @return 操作结果
     */
    @Override
    public Result unfavoriteFile(Long userFileId) {
        return fileFavoriteService.unfavoriteFile(userFileId);
    }
    
    /**
     * 获取收藏的文件列表
     * @return 收藏的文件列表
     */
    @Override
    public Result getFavoriteFiles() {
        return fileFavoriteService.getFavoriteFiles();
    }
}
