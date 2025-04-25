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

import java.util.List;

@Slf4j
@Service
public class FileMoveServiceImpl implements FileMoveService {

    @Autowired
    private FileMapper fileMapper;
    @Override
    public Result moveFile(MoveFileRequest moveFileRequest) {
        Long userFileId = moveFileRequest.getId();
        Long targetFolderId = moveFileRequest.getTargetFolderId();

        // 获取当前用户ID
        Long userId = SecurityUtil.getCurrentUserId();
        if (userId == null) {
            return Result.error("用户未登录");
        }

        // 验证用户是否有权限操作该文件
        UserFile userFile = fileMapper.findUserFileById(userFileId);
        if (userFile == null) {
            return Result.error("文件不存在");
        }
        if (!userFile.getUserId().equals(userId)) {
            return Result.error("没有权限操作该文件");
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

        // 如果是文件夹，检查是否将文件夹移动到自己的子文件夹中
        if (userFile.getFolderType() == 1) {
            // 检查目标文件夹是否是当前文件夹的子文件夹
            List<UserFile> childFolders = fileMapper.checkIsChildFolder(userFileId, targetFolderId);
            if (childFolders != null && !childFolders.isEmpty()) {
                return Result.error("不能将文件夹移动到其子文件夹中");
            }
        }

        // 不允许移动到相同的文件夹
        if (userFile.getFilePid().equals(targetFolderId)) {
            return Result.error("文件已在目标文件夹中");
        }

        // 执行移动操作
        try {
            fileMapper.moveFile(userFileId, targetFolderId);
            return Result.success("移动成功");
        } catch (Exception e) {
            log.error("移动文件失败: {}", e.getMessage(), e);
            return Result.error("移动文件失败");
        }
    }
}
