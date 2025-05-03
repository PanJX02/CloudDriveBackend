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

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

@Slf4j
@Service
public class FileDeleteServiceImpl implements FileDeleteService {
    
    @Autowired
    private FileMapper fileMapper;
    
    // 回收站文件保留31天
    private static final int RECYCLE_BIN_DAYS = 31;
    
    /**
     * 批量将文件或文件夹移动到回收站（软删除）
     * @param fileIds 文件/文件夹ID列表
     * @return 操作结果
     */
    @Override
    @Transactional
    public Result moveToRecycleBin(List<Long> fileIds) {
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
                    moveFileToRecycleBinInternal(userFile);
                } else {
                    // 文件夹递归删除
                    moveFolderToRecycleBinInternal(userFile);
                }
                successCount++;
            } catch (Exception e) {
                log.error("移动ID为{}的文件到回收站失败: {}", fileId, e.getMessage(), e);
                errorMessages.add("移动ID为" + fileId + "的文件到回收站失败");
            }
        }
        
        // 构建结果消息
        if (successCount == 0) {
            return Result.error("操作失败: " + String.join(", ", errorMessages));
        } else if (errorMessages.isEmpty()) {
            return Result.success("成功将" + successCount + "个文件移至回收站");
        } else {
            return Result.success("成功将" + successCount + "个文件移至回收站，但有以下错误: " + String.join(", ", errorMessages));
        }
    }

    
    /**
     * 将单个文件移动到回收站的内部实现（不包含结果封装）
     * @param userFile 用户文件对象
     */
    private void moveFileToRecycleBinInternal(UserFile userFile) {
        // 计算回收站过期时间（当前日期 + 31天的凌晨0点整）
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime expiryDate = now.plusDays(RECYCLE_BIN_DAYS).with(LocalTime.MIDNIGHT);
        long recoveryTime = expiryDate.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli();
        
        // 将文件标记为回收站状态
        fileMapper.moveToRecycleBin(userFile.getId(), recoveryTime);
    }

    
    /**
     * 将文件夹移动到回收站的内部实现（不包含结果封装）
     * @param folderFile 文件夹对象
     */
    private void moveFolderToRecycleBinInternal(UserFile folderFile) {
        // 计算回收站过期时间（当前日期 + 31天的凌晨0点整）
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime expiryDate = now.plusDays(RECYCLE_BIN_DAYS).with(LocalTime.MIDNIGHT);
        long recoveryTime = expiryDate.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli();
        
        // 只将文件夹本身标记为回收站，而不处理其内部文件
        // 这样可以保持回收站列表简洁，只显示顶层删除的项目
        fileMapper.moveToRecycleBin(folderFile.getId(), recoveryTime);
    }
} 