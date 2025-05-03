package com.panjx.clouddrive.task;

import com.panjx.clouddrive.mapper.FileMapper;
import com.panjx.clouddrive.pojo.UserFile;
import com.panjx.clouddrive.service.file.FileDeleteService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * 回收站清理任务，定期清理过期的回收站文件
 */
@Slf4j
@Component
public class RecycleBinCleanupTask {

    @Autowired
    private FileMapper fileMapper;
    
    /**
     * 清理回收站中过期的文件
     * 每天凌晨0点10分执行清理任务，确保能清理当天零点过期的文件
     */
    @Scheduled(cron = "0 10 0 * * ?")
    @Transactional
    public void cleanupExpiredRecycleBinFiles() {
        log.info("开始执行回收站过期文件清理任务");
        
        long currentTime = System.currentTimeMillis();
        
        try {
            // 获取所有已过期的回收站文件
            List<UserFile> expiredFiles = fileMapper.getExpiredRecycleBinFiles(currentTime);
            
            if (expiredFiles != null && !expiredFiles.isEmpty()) {
                log.info("发现{}个过期回收站文件需要清理", expiredFiles.size());
                
                for (UserFile userFile : expiredFiles) {
                    try {
                        // 执行物理删除
                        if (userFile.getFolderType() == 0) {
                            // 文件
                            physicalDeleteFile(userFile);
                        } else {
                            // 文件夹
                            physicalDeleteFolder(userFile);
                        }
                    } catch (Exception e) {
                        log.error("清理回收站文件失败，ID: {}, 错误: {}", userFile.getId(), e.getMessage(), e);
                    }
                }
                
                log.info("回收站过期文件清理完成");
            } else {
                log.info("没有找到过期的回收站文件");
            }
        } catch (Exception e) {
            log.error("回收站清理任务执行失败: {}", e.getMessage(), e);
        }
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