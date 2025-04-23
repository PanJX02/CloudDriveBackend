package com.panjx.clouddrive.service.file.impl;

import com.panjx.clouddrive.mapper.FileMapper;
import com.panjx.clouddrive.pojo.Result;
import com.panjx.clouddrive.pojo.UserFile;
import com.panjx.clouddrive.pojo.request.CopyFileRequest;
import com.panjx.clouddrive.pojo.request.MoveFileRequest;
import com.panjx.clouddrive.pojo.response.FileDetailResponse;
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
    
    /**
     * 获取文件或文件夹的详细信息
     * @param fileId 文件/文件夹ID
     * @return 详细信息结果
     */
    @Override
    public Result getFileDetail(Long fileId) {
        // 检查参数
        if (fileId == null) {
            return Result.error("文件ID不能为空");
        }
        
        // 获取当前用户ID
        Long userId = SecurityUtils.getCurrentUserId();
        if (userId == null) {
            return Result.error("用户未登录");
        }
        
        // 查询文件/文件夹信息
        UserFile userFile = fileMapper.findUserFileById(fileId);
        if (userFile == null) {
            return Result.error("文件不存在");
        }
        
        // 验证是否为当前用户的文件
        if (!userFile.getUserId().equals(userId)) {
            return Result.error("无权访问该文件");
        }
        
        FileDetailResponse detailResponse = new FileDetailResponse();
        detailResponse.setId(userFile.getId());
        detailResponse.setFileName(userFile.getFileName());
        detailResponse.setFileExtension(userFile.getFileExtension());
        detailResponse.setFolderType(userFile.getFolderType());
        detailResponse.setCreateTime(userFile.getCreateTime());
        detailResponse.setLastUpdateTime(userFile.getLastUpdateTime());
        
        // 检查是否为收藏
        detailResponse.setFavoriteFlag(userFile.getFavoriteFlag() != null && userFile.getFavoriteFlag() == 1);
        
        // 如果是文件，获取文件详情
        if (userFile.getFolderType() == 0) {
            // 获取物理文件信息
            if (userFile.getFileId() != null) {
                UserFile fileInfo = fileMapper.findByFileId(userFile.getFileId());
                if (fileInfo != null) {
                    detailResponse.setFileSize(fileInfo.getFileSize());
                    detailResponse.setFileCategory(fileInfo.getFileCategory());
                    
                    // TODO: 如果需要，可以添加文件预览URL
                    // detailResponse.setPreviewUrl("...");
                }
            }
        } 
        // 如果是文件夹，递归计算所有子文件和子文件夹数量及总大小
        else if (userFile.getFolderType() == 1) {
            // 创建统计对象
            FolderStatistics stats = new FolderStatistics();
            
            // 递归计算文件夹统计信息
            calculateFolderStatistics(userFile.getId(), stats);
            
            // 设置响应数据
            detailResponse.setFileCount(stats.getFileCount());
            detailResponse.setFolderCount(stats.getFolderCount());
            detailResponse.setFileSize(stats.getTotalSize());
        }
        
        return Result.success(detailResponse);
    }
    
    /**
     * 用于存储文件夹统计信息的内部类
     */
    private static class FolderStatistics {
        private int fileCount = 0;
        private int folderCount = 0;
        private long totalSize = 0;
        
        public int getFileCount() {
            return fileCount;
        }
        
        public int getFolderCount() {
            return folderCount;
        }
        
        public long getTotalSize() {
            return totalSize;
        }
        
        public void addFileCount(int count) {
            this.fileCount += count;
        }
        
        public void addFolderCount(int count) {
            this.folderCount += count;
        }
        
        public void addSize(long size) {
            this.totalSize += size;
        }
    }
    
    /**
     * 递归计算文件夹统计信息
     * @param folderId 文件夹ID
     * @param stats 统计结果对象
     */
    private void calculateFolderStatistics(Long folderId, FolderStatistics stats) {
        if (folderId == null) {
            return;
        }
        
        // 获取子文件和子文件夹
        List<UserFile> children = fileMapper.findByFilePid(folderId);
        
        if (children == null || children.isEmpty()) {
            return;
        }
        
        for (UserFile child : children) {
            if (child.getFolderType() == 0) { // 文件
                // 增加文件计数
                stats.addFileCount(1);
                
                // 获取文件大小并累加
                if (child.getFileId() != null) {
                    UserFile fileInfo = fileMapper.findByFileId(child.getFileId());
                    if (fileInfo != null && fileInfo.getFileSize() != null) {
                        stats.addSize(fileInfo.getFileSize());
                    }
                }
            } else { // 文件夹
                // 增加文件夹计数
                stats.addFolderCount(1);
                
                // 递归处理子文件夹
                calculateFolderStatistics(child.getId(), stats);
            }
        }
    }
    
    /**
     * 递归计算文件夹大小
     * @param folderId 文件夹ID
     * @return 文件夹总大小（字节）
     */
    private long calculateFolderSize(Long folderId) {
        if (folderId == null) {
            return 0;
        }
        
        // 获取子文件和子文件夹
        List<UserFile> children = fileMapper.findByFilePid(folderId);
        
        if (children == null || children.isEmpty()) {
            return 0;
        }
        
        long totalSize = 0;
        
        for (UserFile child : children) {
            if (child.getFolderType() == 0) { // 文件
                // 获取文件大小并累加
                if (child.getFileId() != null) {
                    UserFile fileInfo = fileMapper.findByFileId(child.getFileId());
                    if (fileInfo != null && fileInfo.getFileSize() != null) {
                        totalSize += fileInfo.getFileSize();
                    }
                }
            } else { // 文件夹
                // 递归计算子文件夹大小
                totalSize += calculateFolderSize(child.getId());
            }
        }
        
        return totalSize;
    }
}
