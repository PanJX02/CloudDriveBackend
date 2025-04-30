package com.panjx.clouddrive.service.file.impl;

import com.panjx.clouddrive.mapper.FileMapper;
import com.panjx.clouddrive.pojo.Result;
import com.panjx.clouddrive.pojo.UserFile;
import com.panjx.clouddrive.pojo.request.CopyFileRequest;
import com.panjx.clouddrive.pojo.request.MoveFileRequest;
import com.panjx.clouddrive.pojo.request.FileSearchRequest;
import com.panjx.clouddrive.pojo.response.FileDetailResponse;
import com.panjx.clouddrive.pojo.response.FileSearchResponse;
import com.panjx.clouddrive.service.file.FileCopyService;
import com.panjx.clouddrive.service.file.FileDeleteService;
import com.panjx.clouddrive.service.file.FileDetailService;
import com.panjx.clouddrive.service.file.FileDownloadService;
import com.panjx.clouddrive.service.file.FileFavoriteService;
import com.panjx.clouddrive.service.file.FileMoveService;
import com.panjx.clouddrive.service.file.FileSearchService;
import com.panjx.clouddrive.service.file.FileService;
import com.panjx.clouddrive.service.file.FileUploadService;
import com.panjx.clouddrive.utils.SecurityUtil;
import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.ArrayList;

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
    private FileDetailService fileDetailService;
    
    @Autowired
    private FileDeleteService fileDeleteService;
    
    @Autowired
    private FileSearchService fileSearchService;

    /**
     * 处理文件上传的操作
     * @param fileName 文件名
     * @param fileExtension 文件后缀名
     * @param fileSHA256 文件的SHA256值
     * @param file_pid 文件的父文件夹ID
     * @return 上传结果
     */
    @Override
    public Result upload(String fileName,String fileExtension, String fileSHA256, Long file_pid) {
        return fileUploadService.upload(fileName,fileExtension,fileSHA256,file_pid);
    }

    /**
     * 处理文件上传完成的操作
     * @param userFile 用户文件对象
     * @return 上传结果
     */
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
     * 批量收藏文件
     * @param userFileIds 用户文件ID列表
     * @return 操作结果
     */
    @Override
    public Result favoriteFiles(List<Long> userFileIds) {
        return fileFavoriteService.favoriteFiles(userFileIds);
    }

    
    /**
     * 批量取消收藏文件
     * @param userFileIds 用户文件ID列表
     * @return 操作结果
     */
    @Override
    public Result unfavoriteFiles(List<Long> userFileIds) {
        return fileFavoriteService.unfavoriteFiles(userFileIds);
    }
    
    /**
     * 获取收藏的文件列表
     * @return 收藏的文件列表
     */
    @Override
    public Result getFavoriteFiles() {
        return fileFavoriteService.getFavoriteFiles();
    }
    
    /**
     * 获取文件或文件夹的详细信息
     * @param fileId 文件/文件夹ID
     * @return 详细信息结果
     */
    @Override
    public Result getFileDetail(Long fileId) {
        return fileDetailService.getFileDetail(fileId);
    }

    
    /**
     * 批量删除文件或文件夹
     * @param fileIds 文件/文件夹ID列表
     * @return 操作结果
     */
    @Override
    public Result deleteFiles(List<Long> fileIds) {
        return fileDeleteService.deleteFiles(fileIds);
    }

    /**
     * 搜索文件
     * @param searchRequest 搜索请求参数
     * @return 搜索结果
     */
    @Override
    public Result searchFiles(FileSearchRequest searchRequest) {
        return fileSearchService.searchFiles(searchRequest);
    }
}
