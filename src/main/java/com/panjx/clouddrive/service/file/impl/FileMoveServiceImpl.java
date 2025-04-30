package com.panjx.clouddrive.service.file.impl;

import com.panjx.clouddrive.mapper.FileMapper;
import com.panjx.clouddrive.pojo.Result;
import com.panjx.clouddrive.pojo.UserFile;
import com.panjx.clouddrive.pojo.request.MoveFileRequest;
import com.panjx.clouddrive.service.file.FileMoveService;
import com.panjx.clouddrive.utils.SecurityUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
public class FileMoveServiceImpl implements FileMoveService {

    @Autowired
    private FileMapper fileMapper;
    
    @Override
    @Transactional
    public Result moveFile(MoveFileRequest moveFileRequest) {
        List<Long> userFileIds = moveFileRequest.getIds();
        Long targetFolderId = moveFileRequest.getTargetFolderId();
        
        // 检查请求参数
        if (userFileIds == null || userFileIds.isEmpty()) {
            return Result.error("未指定要移动的文件");
        }

        // 获取当前用户ID
        Long userId = SecurityUtil.getCurrentUserId();
        if (userId == null) {
            return Result.error("用户未登录");
        }

        // 验证目标文件夹是否存在（如果不是根目录）
        if (targetFolderId != 0) {
            UserFile targetFolder = fileMapper.findUserFileById(targetFolderId);
            if (targetFolder == null) {
                return Result.error("目标文件夹不存在");
            }
            if (!targetFolder.getUserId().equals(userId)) {
                return Result.error("没有权限访问目标文件夹");
            }
            if (targetFolder.getFolderType() != 1) {
                return Result.error("目标位置不是文件夹");
            }
        }
        
        List<String> errorMessages = new ArrayList<>();
        int successCount = 0;
        
        // 遍历处理每个文件/文件夹
        for (Long userFileId : userFileIds) {
            // 验证用户是否有权限操作该文件
            UserFile userFile = fileMapper.findUserFileById(userFileId);
            if (userFile == null) {
                errorMessages.add("ID为" + userFileId + "的文件不存在");
                continue;
            }
            
            if (!userFile.getUserId().equals(userId)) {
                errorMessages.add("没有权限操作ID为" + userFileId + "的文件");
                continue;
            }

            // 不允许移动到相同的文件夹
            if (userFile.getFilePid().equals(targetFolderId)) {
                errorMessages.add("ID为" + userFileId + "的文件已在目标文件夹中");
                continue;
            }

            // 如果是文件夹，检查是否将文件夹移动到自己的子文件夹中
            if (userFile.getFolderType() == 1) {
                // 检查目标文件夹是否是当前文件夹的子文件夹
                List<UserFile> childFolders = fileMapper.checkIsChildFolder(userFileId, targetFolderId);
                if (childFolders != null && !childFolders.isEmpty()) {
                    errorMessages.add("ID为" + userFileId + "的文件夹不能移动到其子文件夹中");
                    continue;
                }
            }

            // 执行移动操作
            try {
                fileMapper.moveFile(userFileId, targetFolderId);
                successCount++;
            } catch (Exception e) {
                log.error("移动ID为{}的文件失败: {}", userFileId, e.getMessage(), e);
                errorMessages.add("移动ID为" + userFileId + "的文件失败");
            }
        }
        
        // 构建结果消息
        if (successCount == userFileIds.size()) {
            return Result.success("所有文件移动成功");
        } else if (successCount > 0) {
            String message = String.format("成功移动%d个文件，%d个文件移动失败", 
                    successCount, userFileIds.size() - successCount);
            return Result.success(message);
        } else {
            return Result.error("所有文件移动失败: " + String.join("; ", errorMessages));
        }
    }
}
