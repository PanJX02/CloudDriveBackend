package com.panjx.clouddrive.service.file.impl;

import com.panjx.clouddrive.mapper.FileMapper;
import com.panjx.clouddrive.pojo.Result;
import com.panjx.clouddrive.pojo.UserFile;
import com.panjx.clouddrive.pojo.response.FileList;
import com.panjx.clouddrive.pojo.response.PageMeta;
import com.panjx.clouddrive.service.file.RecycleBinService;
import com.panjx.clouddrive.utils.SecurityUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

/**
 * 回收站服务实现类
 */
@Slf4j
@Service
public class RecycleBinServiceImpl implements RecycleBinService {

    @Autowired
    private FileMapper fileMapper;
    
    /**
     * 获取回收站文件列表
     * @return 回收站文件列表
     */
    @Override
    public Result getRecycleBinFiles() {
        Long userId = SecurityUtil.getCurrentUserId();
        if (userId == null) {
            return Result.error("用户未登录");
        }
        
        List<UserFile> recycleBinFiles = fileMapper.getRecycleBinFiles(userId);
        
        // 清除敏感数据
        if (recycleBinFiles != null && !recycleBinFiles.isEmpty()) {
            recycleBinFiles.forEach(file -> {
                file.setFileMD5(null);
                file.setFileSHA1(null);
                file.setFileSHA256(null);
                file.setStorageId(null);
                // 保留fileSize以便用户了解文件大小
                file.setFileCover(null);
                file.setReferCount(null);
                file.setStatus(null);
                file.setTranscodeStatus(null);
                file.setFileCreateTime(null);
                file.setLastReferTime(null);
            });
        }
        
        // 创建FileList对象
        FileList fileList = new FileList();
        PageMeta pageMeta = new PageMeta(
            recycleBinFiles != null ? recycleBinFiles.size() : 0,
            1,
            recycleBinFiles != null ? recycleBinFiles.size() : 0,
            1
        );
        fileList.setList(recycleBinFiles);
        fileList.setPageData(pageMeta);
        
        return Result.success("获取回收站文件成功", fileList);
    }
    
    /**
     * 恢复回收站中的文件或文件夹
     * @param fileIds 文件/文件夹ID列表
     * @return 操作结果
     */
    @Override
    @Transactional
    public Result restoreFiles(List<Long> fileIds) {
        if (fileIds == null || fileIds.isEmpty()) {
            return Result.error("未指定要恢复的文件");
        }
        
        Long userId = SecurityUtil.getCurrentUserId();
        if (userId == null) {
            return Result.error("用户未登录");
        }
        
        List<String> errorMessages = new ArrayList<>();
        int successCount = 0;
        
        for (Long fileId : fileIds) {
            UserFile userFile = fileMapper.findUserFileById(fileId);
            if (userFile == null) {
                errorMessages.add("ID为" + fileId + "的文件不存在");
                continue;
            }
            
            if (!userFile.getUserId().equals(userId)) {
                errorMessages.add("无权恢复ID为" + fileId + "的文件");
                continue;
            }
            
            try {
                if (userFile.getDeleteFlag() != 1) {
                    errorMessages.add("ID为" + fileId + "的文件不在回收站中");
                    continue;
                }
                
                if (userFile.getFolderType() == 0) {
                    // 恢复单个文件
                    restoreFile(userFile);
                } else {
                    // 恢复文件夹及其内容
                    restoreFolder(userFile);
                }
                successCount++;
            } catch (Exception e) {
                log.error("恢复ID为{}的文件失败: {}", fileId, e.getMessage(), e);
                errorMessages.add("恢复ID为" + fileId + "的文件失败");
            }
        }
        
        if (successCount == 0) {
            return Result.error("恢复失败: " + String.join(", ", errorMessages));
        } else if (errorMessages.isEmpty()) {
            return Result.success("成功恢复" + successCount + "个文件");
        } else {
            return Result.success("成功恢复" + successCount + "个文件，但有以下错误: " + String.join(", ", errorMessages));
        }
    }
    
    /**
     * 永久删除回收站中的文件或文件夹
     * @param fileIds 文件/文件夹ID列表
     * @return 操作结果
     */
    @Override
    @Transactional
    public Result deleteFilesForever(List<Long> fileIds) {
        if (fileIds == null || fileIds.isEmpty()) {
            return Result.error("未指定要删除的文件");
        }
        
        Long userId = SecurityUtil.getCurrentUserId();
        if (userId == null) {
            return Result.error("用户未登录");
        }
        
        List<String> errorMessages = new ArrayList<>();
        int successCount = 0;
        
        for (Long fileId : fileIds) {
            UserFile userFile = fileMapper.findUserFileById(fileId);
            if (userFile == null) {
                errorMessages.add("ID为" + fileId + "的文件不存在");
                continue;
            }
            
            if (!userFile.getUserId().equals(userId)) {
                errorMessages.add("无权删除ID为" + fileId + "的文件");
                continue;
            }
            
            try {
                if (userFile.getDeleteFlag() != 1) {
                    errorMessages.add("ID为" + fileId + "的文件不在回收站中");
                    continue;
                }
                
                if (userFile.getFolderType() == 0) {
                    // 删除单个文件
                    physicalDeleteFile(userFile);
                } else {
                    // 删除文件夹及其内容
                    physicalDeleteFolder(userFile);
                }
                successCount++;
            } catch (Exception e) {
                log.error("永久删除ID为{}的文件失败: {}", fileId, e.getMessage(), e);
                errorMessages.add("永久删除ID为" + fileId + "的文件失败");
            }
        }
        
        if (successCount == 0) {
            return Result.error("删除失败: " + String.join(", ", errorMessages));
        } else if (errorMessages.isEmpty()) {
            return Result.success("成功永久删除" + successCount + "个文件");
        } else {
            return Result.success("成功永久删除" + successCount + "个文件，但有以下错误: " + String.join(", ", errorMessages));
        }
    }
    
    /**
     * 清空回收站
     * @return 操作结果
     */
    @Override
    @Transactional
    public Result clearRecycleBin() {
        Long userId = SecurityUtil.getCurrentUserId();
        if (userId == null) {
            return Result.error("用户未登录");
        }
        
        try {
            // 获取当前用户回收站中的所有文件
            List<UserFile> recycleBinFiles = fileMapper.getRecycleBinFiles(userId);
            
            if (recycleBinFiles == null || recycleBinFiles.isEmpty()) {
                return Result.success("回收站已经是空的");
            }
            
            int count = 0;
            for (UserFile userFile : recycleBinFiles) {
                try {
                    if (userFile.getFolderType() == 0) {
                        // 删除单个文件
                        physicalDeleteFile(userFile);
                    } else {
                        // 删除文件夹及其内容
                        physicalDeleteFolder(userFile);
                    }
                    count++;
                } catch (Exception e) {
                    log.error("清空回收站时删除文件失败，ID: {}, 错误: {}", userFile.getId(), e.getMessage(), e);
                }
            }
            
            return Result.success("已清空回收站，删除了" + count + "个项目");
        } catch (Exception e) {
            log.error("清空回收站失败: {}", e.getMessage(), e);
            return Result.error("清空回收站失败");
        }
    }
    
    /**
     * 恢复单个文件
     */
    private void restoreFile(UserFile userFile) {
        // 将文件从回收站恢复
        fileMapper.restoreFromRecycleBin(userFile.getId());
    }
    
    /**
     * 恢复文件夹及其内容
     */
    private void restoreFolder(UserFile folderFile) {
        // 获取文件夹下所有内容（包括子文件夹和文件）
        List<UserFile> contents = fileMapper.findAllByFilePidRecursive(folderFile.getId());
        
        // 恢复所有子文件和子文件夹
        for (UserFile file : contents) {
            fileMapper.restoreFromRecycleBin(file.getId());
        }
        
        // 最后恢复文件夹本身
        fileMapper.restoreFromRecycleBin(folderFile.getId());
    }
    
    /**
     * 物理删除单个文件
     */
    private void physicalDeleteFile(UserFile userFile) {
        // 获取文件ID
        Long fileId = userFile.getFileId();
        
        // 删除用户文件关联记录
        fileMapper.deleteUserFile(userFile.getId());
        
        // 减少文件引用计数
        if (fileId != null) {
            fileMapper.decreaseReferCount(fileId);
            
            // 检查引用计数是否为0，如果是则标记为待删除
            UserFile file = fileMapper.findByFileId(fileId);
            if (file != null && file.getReferCount() != null && file.getReferCount() == 0) {
                fileMapper.markFileAsToBeDeleted(fileId);
            }
        }
    }
    
    /**
     * 物理删除文件夹及其内容
     */
    private void physicalDeleteFolder(UserFile folderFile) {
        // 获取文件夹下所有内容（包括子文件夹和文件）
        List<UserFile> contents = fileMapper.findAllByFilePidRecursive(folderFile.getId());
        
        // 先删除所有子文件和子文件夹
        for (UserFile file : contents) {
            // 删除用户文件关联
            fileMapper.deleteUserFile(file.getId());
            
            // 如果是文件，需要处理引用计数
            if (file.getFolderType() == 0 && file.getFileId() != null) {
                fileMapper.decreaseReferCount(file.getFileId());
                
                // 检查引用计数是否为0，如果是则标记为待删除
                UserFile fileInfo = fileMapper.findByFileId(file.getFileId());
                if (fileInfo != null && fileInfo.getReferCount() != null && fileInfo.getReferCount() == 0) {
                    fileMapper.markFileAsToBeDeleted(file.getFileId());
                }
            }
        }
        
        // 最后删除文件夹本身
        fileMapper.deleteUserFile(folderFile.getId());
    }
} 