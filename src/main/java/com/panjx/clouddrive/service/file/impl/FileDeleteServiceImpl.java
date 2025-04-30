package com.panjx.clouddrive.service.file.impl;

import com.panjx.clouddrive.mapper.FileMapper;
import com.panjx.clouddrive.pojo.Result;
import com.panjx.clouddrive.pojo.UserFile;
import com.panjx.clouddrive.service.file.FileDeleteService;
import com.panjx.clouddrive.utils.SecurityUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
public class FileDeleteServiceImpl implements FileDeleteService {
    
    @Autowired
    private FileMapper fileMapper;
    
    /**
     * 删除文件或文件夹
     * @param fileId 文件/文件夹ID
     * @return 操作结果
     */
    @Override
    @Transactional
    public Result deleteFile(Long fileId) {
        // 检查参数
        if (fileId == null) {
            return Result.error("文件ID不能为空");
        }
        
        // 获取当前用户ID
        Long userId = SecurityUtil.getCurrentUserId();
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
            return Result.error("无权删除该文件");
        }
        
        // 根据是文件还是文件夹进行不同的删除操作
        if (userFile.getFolderType() == 0) {
            // 单个文件删除
            return deleteUserFile(userFile);
        } else {
            // 文件夹递归删除
            return deleteFolder(userFile);
        }
    }
    
    /**
     * 批量删除文件或文件夹
     * @param fileIds 文件/文件夹ID列表
     * @return 操作结果
     */
    @Override
    @Transactional
    public Result deleteFiles(List<Long> fileIds) {
        // 检查参数
        if (fileIds == null || fileIds.isEmpty()) {
            return Result.error("未指定要删除的文件");
        }
        
        // 获取当前用户ID
        Long userId = SecurityUtil.getCurrentUserId();
        if (userId == null) {
            return Result.error("用户未登录");
        }
        
        List<String> errorMessages = new ArrayList<>();
        int successCount = 0;
        
        // 遍历处理每个文件/文件夹
        for (Long fileId : fileIds) {
            // 查询文件/文件夹信息
            UserFile userFile = fileMapper.findUserFileById(fileId);
            if (userFile == null) {
                errorMessages.add("ID为" + fileId + "的文件不存在");
                continue;
            }
            
            // 验证是否为当前用户的文件
            if (!userFile.getUserId().equals(userId)) {
                errorMessages.add("无权删除ID为" + fileId + "的文件");
                continue;
            }
            
            try {
                // 根据是文件还是文件夹进行不同的删除操作
                if (userFile.getFolderType() == 0) {
                    // 单个文件删除
                    deleteUserFileInternal(userFile);
                } else {
                    // 文件夹递归删除
                    deleteFolderInternal(userFile);
                }
                successCount++;
            } catch (Exception e) {
                log.error("删除ID为{}的文件失败: {}", fileId, e.getMessage(), e);
                errorMessages.add("删除ID为" + fileId + "的文件失败");
            }
        }
        
        // 构建结果消息
        if (successCount == 0) {
            return Result.error("删除失败: " + String.join(", ", errorMessages));
        } else if (errorMessages.isEmpty()) {
            return Result.success("成功删除" + successCount + "个文件");
        } else {
            return Result.success("成功删除" + successCount + "个文件，但有以下错误: " + String.join(", ", errorMessages));
        }
    }
    
    /**
     * 删除单个文件
     * @param userFile 用户文件对象
     * @return 操作结果
     */
    private Result deleteUserFile(UserFile userFile) {
        try {
            deleteUserFileInternal(userFile);
            return Result.success("文件删除成功");
        } catch (Exception e) {
            log.error("删除文件失败", e);
            return Result.error("删除文件失败：" + e.getMessage());
        }
    }
    
    /**
     * 删除单个文件的内部实现（不包含结果封装）
     * @param userFile 用户文件对象
     */
    private void deleteUserFileInternal(UserFile userFile) {
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
     * 递归删除文件夹
     * @param folderFile 文件夹对象
     * @return 操作结果
     */
    private Result deleteFolder(UserFile folderFile) {
        try {
            deleteFolderInternal(folderFile);
            return Result.success("文件夹删除成功");
        } catch (Exception e) {
            log.error("删除文件夹失败", e);
            return Result.error("删除文件夹失败：" + e.getMessage());
        }
    }
    
    /**
     * 递归删除文件夹的内部实现（不包含结果封装）
     * @param folderFile 文件夹对象
     */
    private void deleteFolderInternal(UserFile folderFile) {
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