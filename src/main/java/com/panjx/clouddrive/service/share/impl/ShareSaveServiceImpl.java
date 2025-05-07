package com.panjx.clouddrive.service.share.impl;

import com.panjx.clouddrive.mapper.FileMapper;
import com.panjx.clouddrive.mapper.ShareMapper;
import com.panjx.clouddrive.mapper.UserMapper;
import com.panjx.clouddrive.pojo.FileShare;
import com.panjx.clouddrive.pojo.Result;
import com.panjx.clouddrive.pojo.ShareItem;
import com.panjx.clouddrive.pojo.User;
import com.panjx.clouddrive.pojo.UserFile;
import com.panjx.clouddrive.service.share.ShareSaveService;
import com.panjx.clouddrive.utils.SecurityUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
@Service
public class ShareSaveServiceImpl implements ShareSaveService {

    @Autowired
    private ShareMapper shareMapper;
    
    @Autowired
    private FileMapper fileMapper;

    @Autowired
    private UserMapper userMapper;

    /**
     * 保存分享的文件到自己的云盘
     * @param shareId 分享ID
     * @param code 提取码
     * @param fileIds 要保存的文件ID列表
     * @param targetFolderId 目标文件夹ID (保存到的位置)
     * @return 保存结果
     */
    @Override
    @Transactional
    public Result saveShareFiles(Long shareId, String code, List<Long> fileIds, Long targetFolderId) {
        log.info("保存分享文件, shareId: {}, fileIds: {}, targetFolderId: {}", shareId, fileIds, targetFolderId);
        
        // 1. 检查参数
        if (fileIds == null || fileIds.isEmpty()) {
            return Result.error("请选择要保存的文件");
        }
        
        // 2. 获取当前用户ID
        Long userId = SecurityUtil.getCurrentUserId();
        if (userId == null) {
            return Result.error("用户未登录");
        }
        
        // 3. 检查分享是否存在且有效
        FileShare share = shareMapper.findShareById(shareId);
        if (share == null) {
            log.error("分享不存在: {}", shareId);
            return Result.error("分享不存在或已失效");
        }
        
        // 4. 检查分享是否过期
        long currentTime = System.currentTimeMillis();
        if (share.getValidType() != 3 && share.getExpireTime() < currentTime) {
            // 已过期，更新状态
            if (share.getIsExpired() == 0) {
                share.setIsExpired(1);
                shareMapper.updateExpiredStatus(share.getShareId(), 1);
                log.info("更新分享过期状态: shareId={}, isExpired=1", share.getShareId());
            }
            return Result.error("分享已过期");
        }
        
        // 5. 检查提取码是否正确
        if (share.getCode() != null && !share.getCode().isEmpty()) {
            if (code == null || !code.equals(share.getCode())) {
                log.error("提取码错误: {}", code);
                return Result.error("提取码错误");
            }
        }
        
        // 6. 检查目标文件夹是否存在且属于当前用户
        if (targetFolderId != 0) { // 0表示根目录
            UserFile targetFolder = fileMapper.findUserFileById(targetFolderId);
            if (targetFolder == null) {
                return Result.error("目标文件夹不存在");
            }
            if (!targetFolder.getUserId().equals(userId)) {
                return Result.error("无权访问目标文件夹");
            }
            if (targetFolder.getFolderType() != 1) {
                return Result.error("目标位置不是文件夹");
            }
        }
        
        // 7. 获取分享的所有文件
        List<ShareItem> shareItems = shareMapper.findShareItemsByShareId(shareId);
        if (shareItems == null || shareItems.isEmpty()) {
            return Result.error("分享内容为空");
        }
        
        // 8. 构建分享文件ID集合，用于验证fileIds是否都属于该分享
        Set<Long> shareFileIdSet = shareItems.stream()
                .map(ShareItem::getUserFileId)
                .collect(Collectors.toSet());
        
        // 验证所有请求保存的文件ID是否都属于分享内容
        for (Long fileId : fileIds) {
            if (!isFileInShare(fileId, shareFileIdSet, fileMapper)) {
                return Result.error("存在不属于该分享的文件");
            }
        }
        
        // 9. 获取要保存的文件信息
        List<UserFile> sourceFiles = fileMapper.findUserFilesByIds(fileIds);
        if (sourceFiles == null || sourceFiles.isEmpty()) {
            return Result.error("获取分享文件信息失败");
        }
        
        // 10. 计算需要保存的文件总大小，检查用户可用空间是否足够
        long totalRequiredSpace = 0;
        for (UserFile sourceFile : sourceFiles) {
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
        }
        
        // 检查用户可用空间是否足够
        if (!checkUserSpaceEnough(userId, totalRequiredSpace)) {
            return Result.error("存储空间不足，无法保存分享文件");
        }
        
        // 11. 执行保存操作（复制文件）
        int successCount = 0;
        List<String> failedFiles = new ArrayList<>();
        
        for (UserFile sourceFile : sourceFiles) {
            try {
                // 复制文件或文件夹
                if (sourceFile.getFolderType() == 1) {
                    // 复制文件夹
                    copyFolder(sourceFile, targetFolderId, userId);
                } else {
                    // 复制文件
                    copySimpleFile(sourceFile, targetFolderId, userId);
                }
                successCount++;
            } catch (Exception e) {
                log.error("保存分享文件失败: {}，错误: {}", sourceFile.getFileName(), e.getMessage(), e);
                failedFiles.add(sourceFile.getFileName() + (sourceFile.getFileExtension() != null ? "." + sourceFile.getFileExtension() : ""));
            }
        }
        
        // 12. 返回结果
        if (successCount == 0) {
            return Result.error("所有文件保存失败");
        } else if (failedFiles.isEmpty()) {
            return Result.success("成功保存" + successCount + "个文件");
        } else {
            return Result.success("成功保存" + successCount + "个文件，失败" + failedFiles.size() + "个");
        }
    }
    
    /**
     * 递归检查文件是否属于分享内容
     * 检查逻辑：
     * 1. 文件ID直接在分享列表中
     * 2. 文件的某个父文件夹在分享列表中
     * 
     * 递归终止条件：
     * 1. 成功终止：在向上递归过程中找到了属于分享列表的父文件夹
     * 2. 失败终止：递归到根目录(pid=0)或找不到父文件
     * 
     * @param fileId 待检查的文件ID
     * @param shareFileIdSet 分享文件ID集合
     * @param fileMapper 文件Mapper
     * @return 是否属于分享内容
     */
    private boolean isFileInShare(Long fileId, Set<Long> shareFileIdSet, FileMapper fileMapper) {
        // 1. 文件ID直接在分享列表中
        if (shareFileIdSet.contains(fileId)) {
            return true;
        }
        
        // 2. 递归检查父文件夹是否在分享列表中
        UserFile currentFile = fileMapper.findUserFileById(fileId);
        if (currentFile == null) {
            return false;
        }
        
        // 递归向上查找，直到根目录（pid=0）
        Long parentId = currentFile.getFilePid();
        while (parentId != null && parentId != 0) {
            // 父文件夹在分享列表中
            if (shareFileIdSet.contains(parentId)) {
                return true;
            }
            
            // 继续向上查找
            UserFile parentFile = fileMapper.findUserFileById(parentId);
            if (parentFile == null) {
                break;
            }
            parentId = parentFile.getFilePid();
        }
        
        return false;
    }

    /**
     * 复制文件夹及其内容到目标位置
     * @param sourceFolder 源文件夹
     * @param targetFolderId 目标文件夹ID
     * @param userId 目标用户ID
     * @return 新文件夹ID
     */
    private Long copyFolder(UserFile sourceFolder, Long targetFolderId, Long userId) {
        // 1. 创建新文件夹
        UserFile newFolder = new UserFile();
        newFolder.setUserId(userId);
        newFolder.setFileId(null); // 文件夹没有关联的物理文件
        newFolder.setFileName(sourceFolder.getFileName());
        newFolder.setFileExtension(null);
        newFolder.setFilePid(targetFolderId);
        newFolder.setFolderType(1); // 1表示文件夹
        newFolder.setDeleteFlag(0);
        newFolder.setCreateTime(System.currentTimeMillis());
        newFolder.setLastUpdateTime(System.currentTimeMillis());

        // 添加新文件夹记录
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
                    // 递归复制子文件夹
                    copyFolder(child, newFolderId, userId);
                } else {
                    // 复制子文件
                    copySimpleFile(child, newFolderId, userId);
                }
            }
        }

        return newFolderId;
    }

    /**
     * 复制单个文件到目标位置
     * @param sourceFile 源文件
     * @param targetFolderId 目标文件夹ID
     * @param userId 目标用户ID
     * @return 新文件ID
     */
    private Long copySimpleFile(UserFile sourceFile, Long targetFolderId, Long userId) {
        // 创建新的用户文件记录
        UserFile newUserFile = new UserFile();
        newUserFile.setUserId(userId);
        newUserFile.setFileId(sourceFile.getFileId());
        newUserFile.setFileName(sourceFile.getFileName());
        newUserFile.setFileExtension(sourceFile.getFileExtension());
        newUserFile.setFilePid(targetFolderId);
        newUserFile.setFolderType(0); // 0表示文件
        newUserFile.setDeleteFlag(0);
        newUserFile.setCreateTime(System.currentTimeMillis());
        newUserFile.setLastUpdateTime(System.currentTimeMillis());

        // 增加文件引用计数
        fileMapper.increaseReferCount(sourceFile.getFileId(), System.currentTimeMillis());

        // 添加新的文件记录
        fileMapper.addUserFile(newUserFile);
        
        // 更新用户使用空间
        if (userMapper != null) {
            UserFile fileInfo = fileMapper.findByFileId(sourceFile.getFileId());
            if (fileInfo != null && fileInfo.getFileSize() != null) {
                userMapper.updateUserSpace(userId, fileInfo.getFileSize());
            }
        }

        return newUserFile.getId();
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
} 