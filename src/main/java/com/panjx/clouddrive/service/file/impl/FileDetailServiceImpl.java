package com.panjx.clouddrive.service.file.impl;

import com.panjx.clouddrive.mapper.FileMapper;
import com.panjx.clouddrive.pojo.Result;
import com.panjx.clouddrive.pojo.UserFile;
import com.panjx.clouddrive.pojo.response.FileDetailResponse;
import com.panjx.clouddrive.service.file.FileDetailService;
import com.panjx.clouddrive.utils.SecurityUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
public class FileDetailServiceImpl implements FileDetailService {
    
    @Autowired
    private FileMapper fileMapper;

    
    /**
     * 批量获取文件或文件夹的详细信息
     * @param fileIds 文件/文件夹ID列表
     * @return 详细信息结果
     */
    @Override
    public Result getFileDetails(List<Long> fileIds) {
        // 检查参数
        if (fileIds == null || fileIds.isEmpty()) {
            return Result.error("文件ID列表不能为空");
        }
        
        // 获取当前用户ID
        Long userId = SecurityUtil.getCurrentUserId();
        if (userId == null) {
            return Result.error("用户未登录");
        }
        
        // 1. 批量查询请求的文件信息（包含 file 表数据）
        List<UserFile> requestedUserFiles = fileMapper.findUserFilesByIds(fileIds);
        
        // 2. 验证权限并过滤无效文件
        List<UserFile> validUserFiles = new ArrayList<>();
        List<String> errorMessages = new ArrayList<>();
        Set<Long> fetchedIds = new HashSet<>();
        
        for (UserFile userFile : requestedUserFiles) {
            if (!userFile.getUserId().equals(userId)) {
                errorMessages.add("无权访问ID为" + userFile.getId() + "的文件");
            } else {
                validUserFiles.add(userFile);
                fetchedIds.add(userFile.getId());
            }
        }
        
        // 记录未查询到的文件ID
        for (Long requestedId : fileIds) {
            if (!fetchedIds.contains(requestedId)) {
                errorMessages.add("ID为" + requestedId + "的文件不存在");
            }
        }
        
        // 如果没有找到任何有效文件
        if (validUserFiles.isEmpty()) {
            return Result.error("未能获取任何有效文件的详情: " + String.join(", ", errorMessages));
        }
        
        // 3. 处理单个有效文件的情况
        if (validUserFiles.size() == 1) {
            UserFile singleFile = validUserFiles.get(0);
            FileDetailResponse detailResponse;
            if (singleFile.getFolderType() == 0) {
                // 对于单个文件，构建包含完整物理信息的响应
                detailResponse = buildSingleFileDetailResponse(singleFile);
            } else {
                // 对于单个文件夹，构建包含统计信息的响应
                detailResponse = buildSingleFolderDetailResponse(singleFile);
            }
             Map<String, Object> singleResult = new HashMap<>();
             singleResult.put("detail", detailResponse);
             if (!errorMessages.isEmpty()) {
                 singleResult.put("errors", errorMessages);
             }
             return Result.success(singleResult);
        }
        
        // 4. 处理多个有效文件的情况
        FileDetailResponse summaryResponse = calculateSummaryStatistics(validUserFiles);
        summaryResponse.setFileName(determineSummaryFileName(validUserFiles, fileIds.size()));
        
        // 构建结果
        Map<String, Object> result = new HashMap<>();
        result.put("detail", summaryResponse);
        if (!errorMessages.isEmpty()) {
            result.put("errors", errorMessages);
        }
        
        return Result.success(result);
    }
    
    /**
     * 为单个文件构建详细响应（包含物理文件信息）
     */
    private FileDetailResponse buildSingleFileDetailResponse(UserFile userFile) {
        FileDetailResponse response = new FileDetailResponse();
        response.setId(userFile.getId());
        response.setFileName(userFile.getFileName());
        response.setFileExtension(userFile.getFileExtension());
        response.setFolderType(userFile.getFolderType());
        response.setCreateTime(userFile.getCreateTime());
        response.setLastUpdateTime(userFile.getLastUpdateTime());
        response.setFavoriteFlag(userFile.getFavoriteFlag() != null && userFile.getFavoriteFlag() == 1);

        // 从已连接查询的 UserFile 对象中获取物理文件信息
        response.setFileSize(userFile.getFileSize());
        response.setFileCategory(userFile.getFileCategory());
        response.setFileMd5(userFile.getFileMD5());
        response.setFileSha1(userFile.getFileSHA1());
        response.setFileSha256(userFile.getFileSHA256());

        return response;
    }
    
    /**
     * 为单个文件夹构建详细响应（包含统计信息）
     */
    private FileDetailResponse buildSingleFolderDetailResponse(UserFile userFile) {
        FileDetailResponse response = new FileDetailResponse();
        response.setId(userFile.getId());
        response.setFileName(userFile.getFileName());
        response.setFileExtension(userFile.getFileExtension());
        response.setFolderType(userFile.getFolderType());
        response.setCreateTime(userFile.getCreateTime());
        response.setLastUpdateTime(userFile.getLastUpdateTime());
        response.setFavoriteFlag(userFile.getFavoriteFlag() != null && userFile.getFavoriteFlag() == 1);
        
        FolderStatistics stats = new FolderStatistics();
        calculateFolderStatistics(userFile.getId(), stats);
        response.setFileCount(stats.getFileCount());
        response.setFolderCount(stats.getFolderCount());
        response.setFileSize(stats.getTotalSize());
        
        return response;
    }
    
    /**
     * 计算多个文件的汇总统计信息
     */
    private FileDetailResponse calculateSummaryStatistics(List<UserFile> validUserFiles) {
        FileDetailResponse summaryResponse = new FileDetailResponse();
        summaryResponse.setFileCount(0);
        summaryResponse.setFolderCount(0);
        summaryResponse.setFileSize(0L);
        long currentTime = System.currentTimeMillis();
        summaryResponse.setCreateTime(currentTime); // 使用当前时间作为汇总项的创建/更新时间
        summaryResponse.setLastUpdateTime(currentTime);
        
        for (UserFile userFile : validUserFiles) {
            if (userFile.getFolderType() == 0) {
                summaryResponse.setFileCount(summaryResponse.getFileCount() + 1);
                if (userFile.getFileSize() != null) { // fileSize 来自连接查询
                    summaryResponse.setFileSize(summaryResponse.getFileSize() + userFile.getFileSize());
                }
            } else {
                summaryResponse.setFolderCount(summaryResponse.getFolderCount() + 1);
                FolderStatistics stats = new FolderStatistics();
                calculateFolderStatistics(userFile.getId(), stats);
                summaryResponse.setFileCount(summaryResponse.getFileCount() + stats.getFileCount());
                summaryResponse.setFolderCount(summaryResponse.getFolderCount() + stats.getFolderCount()); // 文件夹自身已计，这里加子文件夹
                summaryResponse.setFileSize(summaryResponse.getFileSize() + stats.getTotalSize());
            }
        }
        return summaryResponse;
    }
    
    /**
     * 确定多个文件/文件夹的汇总文件名
     */
    private String determineSummaryFileName(List<UserFile> validUserFiles, int originalRequestSize) {
        if (validUserFiles.isEmpty()) {
            return "多个文件"; // 不应发生，但作为防御
        }
        
        UserFile firstValidFile = validUserFiles.get(0);
        boolean hasFolder = validUserFiles.stream().anyMatch(f -> f.getFolderType() == 1);
        
        if (hasFolder) {
            UserFile firstFolder = validUserFiles.stream()
                                        .filter(f -> f.getFolderType() == 1)
                                        .findFirst()
                                        .orElse(firstValidFile); // 如果全是文件但hasFolder误判，则回退
            return firstFolder.getFileName() + " 等" + originalRequestSize + "个文件(夹)";
        } else {
            // 全是文件
            String fileName = firstValidFile.getFileName();
            if (firstValidFile.getFileExtension() != null && !firstValidFile.getFileExtension().isEmpty()) {
                fileName = fileName + "." + firstValidFile.getFileExtension();
            }
            return fileName + " 等" + originalRequestSize + "个文件";
        }
    }

    
    /**
     * 根据文件对象构建详情响应
     * @param userFile 用户文件对象（只包含 user_file 表信息）
     * @return 文件详情响应
     */
    private FileDetailResponse buildFileDetailResponse(UserFile userFile) {
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
                // 查询 file 表获取详细信息
                UserFile fileInfo = fileMapper.findByFileId(userFile.getFileId());
                if (fileInfo != null) {
                    // 设置基本文件信息
                    detailResponse.setFileSize(fileInfo.getFileSize());
                    detailResponse.setFileCategory(fileInfo.getFileCategory());
                    
                    // 设置哈希值信息
                    detailResponse.setFileMd5(fileInfo.getFileMD5());
                    detailResponse.setFileSha1(fileInfo.getFileSHA1());
                    detailResponse.setFileSha256(fileInfo.getFileSHA256());

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
        
        return detailResponse;
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
        
        public void addFile() {
            fileCount++;
        }
        
        public void addFolder() {
            folderCount++;
        }
        
        public void addSize(Long size) {
            if (size != null) {
                totalSize += size;
            }
        }
    }
    
    /**
     * 递归计算文件夹统计信息
     * @param folderId 文件夹ID
     * @param stats 统计对象
     */
    private void calculateFolderStatistics(Long folderId, FolderStatistics stats) {
        // 获取文件夹下的所有文件和子文件夹
        List<UserFile> childFiles = fileMapper.findByFilePid(folderId);
        
        if (childFiles != null && !childFiles.isEmpty()) {
            for (UserFile childFile : childFiles) {
                // 如果是文件
                if (childFile.getFolderType() == 0) {
                    stats.addFile();
                    
                    // 获取文件大小 (findByFilePid 已经连接了 file 表)
                    if (childFile.getFileSize() != null) {
                        stats.addSize(childFile.getFileSize());
                    }
                } 
                // 如果是文件夹
                else if (childFile.getFolderType() == 1) {
                    stats.addFolder();
                    
                    // 递归处理子文件夹
                    calculateFolderStatistics(childFile.getId(), stats);
                }
            }
        }
    }
} 