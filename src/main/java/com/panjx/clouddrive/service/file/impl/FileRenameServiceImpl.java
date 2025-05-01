package com.panjx.clouddrive.service.file.impl;

import com.panjx.clouddrive.mapper.FileMapper;
import com.panjx.clouddrive.pojo.Result;
import com.panjx.clouddrive.pojo.UserFile;
import com.panjx.clouddrive.pojo.request.RenameFileRequest;
import com.panjx.clouddrive.service.file.FileRenameService;
import com.panjx.clouddrive.utils.SecurityUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

@Slf4j
@Service
public class FileRenameServiceImpl implements FileRenameService {

    @Autowired
    private FileMapper fileMapper;

    /**
     * 重命名文件或文件夹
     * @param renameFileRequest 重命名请求参数
     * @return 操作结果
     */
    @Override
    @Transactional
    public Result renameFile(RenameFileRequest renameFileRequest) {
        // 检查参数
        if (renameFileRequest.getId() == null) {
            return Result.error("文件ID不能为空");
        }

        if (!StringUtils.hasText(renameFileRequest.getNewFileName())) {
            return Result.error("新文件名不能为空");
        }

        // 获取当前用户ID
        Long userId = SecurityUtil.getCurrentUserId();
        if (userId == null) {
            return Result.error("用户未登录");
        }

        // 查询文件/文件夹信息
        UserFile userFile = fileMapper.findUserFileById(renameFileRequest.getId());
        if (userFile == null) {
            return Result.error("文件不存在");
        }

        // 验证是否为当前用户的文件
        if (!userFile.getUserId().equals(userId)) {
            return Result.error("无权重命名该文件");
        }

        // 检查同目录下是否已存在同名文件/文件夹
        String newFileName = renameFileRequest.getNewFileName();
        
        // 如果是文件，需要处理文件扩展名
        String fileExtension = userFile.getFileExtension();
        if (userFile.getFolderType() == 0 && StringUtils.hasText(fileExtension)) {
            // 如果新文件名包含扩展名，则从新文件名中提取扩展名
            if (newFileName.contains(".")) {
                int lastDotIndex = newFileName.lastIndexOf(".");
                fileExtension = newFileName.substring(lastDotIndex + 1);
                newFileName = newFileName.substring(0, lastDotIndex);
            }
        }

        // 更新文件名和更新时间
        userFile.setFileName(newFileName);
        if (userFile.getFolderType() == 0) {
            userFile.setFileExtension(fileExtension);
        }
        userFile.setLastUpdateTime(System.currentTimeMillis());

        // 执行更新操作
        int result = fileMapper.updateFileName(userFile);
        if (result > 0) {
            return Result.success("重命名成功");
        } else {
            return Result.error("重命名失败");
        }
    }
} 