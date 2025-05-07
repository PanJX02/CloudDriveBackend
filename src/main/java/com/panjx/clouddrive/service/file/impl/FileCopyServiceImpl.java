package com.panjx.clouddrive.service.file.impl;

import com.panjx.clouddrive.mapper.FileMapper;
import com.panjx.clouddrive.mapper.UserMapper;
import com.panjx.clouddrive.pojo.Result;
import com.panjx.clouddrive.pojo.User;
import com.panjx.clouddrive.pojo.UserFile;
import com.panjx.clouddrive.pojo.request.CopyFileRequest;
import com.panjx.clouddrive.service.file.FileCopyService;
import com.panjx.clouddrive.utils.SecurityUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
public class FileCopyServiceImpl implements FileCopyService {

    @Autowired
    private FileMapper fileMapper;
    
    @Autowired
    private UserMapper userMapper;

    @Override
    @Transactional
    public Result copyFile(CopyFileRequest copyFileRequest) {
        List<Long> userFileIds = copyFileRequest.getIds();
        Long targetFolderId = copyFileRequest.getTargetFolderId();
        
        // 检查请求参数
        if (userFileIds == null || userFileIds.isEmpty()) {
            return Result.error("未指定要复制的文件");
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
        
        // 计算需要复制的总空间大小
        long totalRequiredSpace = 0;
        List<UserFile> filesToCopy = new ArrayList<>();
        
        // 检查并计算所有文件所需的总空间
        for (Long sourceId : userFileIds) {
            UserFile sourceFile = fileMapper.findUserFileById(sourceId);
            if (sourceFile == null) {
                continue;
            }
            
            if (!sourceFile.getUserId().equals(userId)) {
                continue;
            }
            
            if (sourceFile.getFolderType() == 1) {
                // 对于文件夹，需要递归计算文件夹内所有文件的大小
                totalRequiredSpace += calculateFolderSize(sourceFile.getId());
            } else {
                // 对于单个文件，直接获取文件大小
                UserFile fileInfo = fileMapper.findByFileId(sourceFile.getFileId());
                if (fileInfo != null && fileInfo.getFileSize() != null) {
                    totalRequiredSpace += fileInfo.getFileSize();
                }
            }
            
            filesToCopy.add(sourceFile);
        }
        
        // 检查用户可用空间是否足够
        if (!checkUserSpaceEnough(userId, totalRequiredSpace)) {
            return Result.error("存储空间不足，无法完成复制操作");
        }
        
        List<String> errorMessages = new ArrayList<>();
        int successCount = 0;
        
        // 遍历处理每个文件/文件夹
        for (UserFile sourceFile : filesToCopy) {
            Long sourceId = sourceFile.getId();
            
            // 如果是文件夹，检查是否将文件夹复制到自己的子文件夹中
            if (sourceFile.getFolderType() == 1) {
                // 检查目标文件夹是否是当前文件夹的子文件夹
                List<UserFile> childFolders = fileMapper.checkIsChildFolder(sourceId, targetFolderId);
                if (childFolders != null && !childFolders.isEmpty()) {
                    errorMessages.add("ID为" + sourceId + "的文件夹不能复制到其子文件夹中");
                    continue;
                }
            }

            // 执行复制操作
            try {
                boolean needRename = sourceFile.getFilePid().equals(targetFolderId);
                
                if (sourceFile.getFolderType() == 1) {
                    // 复制文件夹及其内容
                    copyFolder(sourceFile, targetFolderId, needRename);
                } else {
                    // 复制文件
                    copySimpleFile(sourceFile, targetFolderId, needRename);
                }
                successCount++;
            } catch (Exception e) {
                log.error("复制ID为{}的文件失败: {}", sourceId, e.getMessage(), e);
                errorMessages.add("复制ID为" + sourceId + "的文件失败: " + e.getMessage());
            }
        }
        
        // 构建结果消息
        if (successCount == filesToCopy.size()) {
            return Result.success("所有文件复制成功");
        } else if (successCount > 0) {
            String message = String.format("成功复制%d个文件，%d个文件复制失败", 
                    successCount, filesToCopy.size() - successCount);
            return Result.success(message);
        } else {
            return Result.error("所有文件复制失败: " + String.join("; ", errorMessages));
        }
    }

    /**
     * 计算文件夹内所有文件的总大小
     * @param folderId 文件夹ID
     * @return 总大小（字节）
     */
    private long calculateFolderSize(Long folderId) {
        long totalSize = 0;
        
        // 获取文件夹下所有文件和子文件夹
        List<UserFile> children = fileMapper.findByFilePid(folderId);
        if (children != null && !children.isEmpty()) {
            for (UserFile child : children) {
                if (child.getFolderType() == 1) {
                    // 递归计算子文件夹大小
                    totalSize += calculateFolderSize(child.getId());
                } else {
                    // 累加文件大小
                    UserFile fileInfo = fileMapper.findByFileId(child.getFileId());
                    if (fileInfo != null && fileInfo.getFileSize() != null) {
                        totalSize += fileInfo.getFileSize();
                    }
                }
            }
        }
        
        return totalSize;
    }
    
    /**
     * 检查用户可用空间是否足够
     * @param userId 用户ID
     * @param requiredSpace 需要的空间大小（字节）
     * @return 空间是否足够
     */
    private boolean checkUserSpaceEnough(Long userId, long requiredSpace) {
        if (requiredSpace <= 0) {
            return true; // 不需要额外空间
        }
        
        User user = userMapper.findById(userId);
        if (user == null) {
            return false;
        }
        
        // 使用User对象的getAvailableSpace方法计算可用空间
        Long availableSpace = user.getAvailableSpace();
        
        log.info("用户ID: {}, 需要空间: {}字节, 可用空间: {}字节", userId, requiredSpace, availableSpace);
        
        return availableSpace >= requiredSpace;
    }

    /**
     * 复制文件夹及其内容
     * @param sourceFolder 源文件夹
     * @param targetFolderId 目标文件夹ID
     * @param needRename 是否需要重命名（同文件夹复制时）
     * @return 新文件夹的ID
     */
    private Long copyFolder(UserFile sourceFolder, Long targetFolderId, boolean needRename) {
        // 1. 创建新文件夹
        UserFile newFolder = new UserFile();
        newFolder.setUserId(sourceFolder.getUserId());
        newFolder.setFileId(null); // 文件夹没有关联的物理文件
        newFolder.setFileName(needRename ? sourceFolder.getFileName() + "副本" : sourceFolder.getFileName());
        newFolder.setFileExtension(null);
        newFolder.setFilePid(targetFolderId);
        newFolder.setFolderType(1); // 1表示文件夹
        newFolder.setDeleteFlag(0);
        newFolder.setCreateTime(System.currentTimeMillis());
        newFolder.setLastUpdateTime(System.currentTimeMillis());

        // 添加新文件夹记录（此处会自动设置newFolder的id属性）
        fileMapper.addUserFile(newFolder);
        Long newFolderId = newFolder.getId();
        log.info("创建新文件夹，ID: {}, 名称: {}, 父文件夹ID: {}", newFolderId, newFolder.getFileName(), targetFolderId);

        // 2. 获取源文件夹下所有文件和子文件夹
        List<UserFile> children = fileMapper.findByFilePid(sourceFolder.getId());
        if (children != null && !children.isEmpty()) {
            log.info("文件夹 {} 下有 {} 个子项需要复制", sourceFolder.getFileName(), children.size());
            
            // 3. 递归复制所有子文件和文件夹
            for (UserFile child : children) {
                if (child.getFolderType() == 1) {
                    // 递归复制子文件夹，注意使用newFolderId作为目标父ID
                    Long newChildFolderId = copyFolder(child, newFolderId, false);
                    log.info("复制子文件夹 {} 到新文件夹 {}, 新ID: {}", child.getFileName(), newFolderId, newChildFolderId);
                } else {
                    // 复制子文件，使用newFolderId作为目标父ID
                    Long newFileId = copySimpleFile(child, newFolderId, false);
                    log.info("复制文件 {} 到新文件夹 {}, 新ID: {}", child.getFileName(), newFolderId, newFileId);
                }
            }
        } else {
            log.info("文件夹 {} 为空，无需复制子项", sourceFolder.getFileName());
        }

        return newFolderId;
    }

    /**
     * 复制单个文件
     * @param sourceFile 源文件
     * @param targetFolderId 目标文件夹ID
     * @param needRename 是否需要重命名（同文件夹复制时）
     * @return 新文件的ID
     */
    private Long copySimpleFile(UserFile sourceFile, Long targetFolderId, boolean needRename) {
        // 创建新的用户文件记录
        UserFile newUserFile = new UserFile();
        // 设置用户ID
        newUserFile.setUserId(sourceFile.getUserId());
        // 设置文件ID（引用相同的物理文件）
        newUserFile.setFileId(sourceFile.getFileId());
        // 设置文件名（如需重命名则添加"副本"）
        newUserFile.setFileName(needRename ? sourceFile.getFileName() + "副本" : sourceFile.getFileName());
        // 设置文件扩展名
        newUserFile.setFileExtension(sourceFile.getFileExtension());
        // 设置父文件夹ID
        newUserFile.setFilePid(targetFolderId);
        // 设置为文件类型
        newUserFile.setFolderType(0);
        // 设置为正常状态
        newUserFile.setDeleteFlag(0);
        // 设置创建时间为当前时间
        newUserFile.setCreateTime(System.currentTimeMillis());
        // 设置最后更新时间为当前时间
        newUserFile.setLastUpdateTime(System.currentTimeMillis());

        // 增加文件引用计数
        fileMapper.increaseReferCount(sourceFile.getFileId(), System.currentTimeMillis());

        // 添加新的文件记录，自动设置newUserFile的id属性
        fileMapper.addUserFile(newUserFile);
        
        // 更新用户使用空间
        UserFile fileInfo = fileMapper.findByFileId(sourceFile.getFileId());
        if (fileInfo != null && fileInfo.getFileSize() != null) {
            userMapper.updateUserSpace(sourceFile.getUserId(), fileInfo.getFileSize());
        }

        return newUserFile.getId();
    }
} 