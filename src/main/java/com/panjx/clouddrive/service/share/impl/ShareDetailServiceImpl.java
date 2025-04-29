package com.panjx.clouddrive.service.share.impl;

import com.panjx.clouddrive.mapper.FileMapper;
import com.panjx.clouddrive.mapper.ShareMapper;
import com.panjx.clouddrive.pojo.FileShare;
import com.panjx.clouddrive.pojo.ShareItem;
import com.panjx.clouddrive.pojo.UserFile;
import com.panjx.clouddrive.pojo.response.FileList;
import com.panjx.clouddrive.pojo.response.PageMeta;
import com.panjx.clouddrive.service.share.ShareDetailService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
public class ShareDetailServiceImpl implements ShareDetailService {

    @Autowired
    private ShareMapper shareMapper;
    
    @Autowired
    private FileMapper fileMapper;

    @Override
    public FileList getShareFiles(Long shareId, String code, Long folderId) {
        log.info("获取分享文件列表, shareId: {}, code: {}, folderId: {}", shareId, code, folderId);
        
        // 1. 检查分享是否存在
        FileShare share = shareMapper.findShareById(shareId);
        if (share == null) {
            log.error("分享不存在: {}", shareId);
            return null;
        }
        
        // 2. 检查分享是否过期
        long currentTime = System.currentTimeMillis();
        if (share.getValidType() != 3 && share.getExpireTime() < currentTime) {
            // 已过期，更新状态
            if (share.getIsExpired() == 0) {
                share.setIsExpired(1);
                shareMapper.updateExpiredStatus(share.getShareId(), 1);
                log.info("更新分享过期状态: shareId={}, isExpired=1", share.getShareId());
            }
            log.error("分享已过期: {}", shareId);
            return null;
        }
        
        // 3. 检查提取码是否正确
        if (share.getCode() != null && !share.getCode().isEmpty()) {
            if (code == null || !code.equals(share.getCode())) {
                log.error("提取码错误: {}", code);
                return null;
            }
        }
        
        // 4. 增加查看次数（仅在首次访问或未指定folderId时增加）
        if (folderId == null) {
            shareMapper.updateShowCount(share.getShareId(), share.getShowCount() + 1);
        }
        
        // 5. 获取分享项对应的用户文件ID列表
        List<ShareItem> shareItems = shareMapper.findShareItemsByShareId(shareId);
        if (shareItems == null || shareItems.isEmpty()) {
            log.error("分享项为空: {}", shareId);
            return null;
        }
        
        // 提取用户文件ID列表
        List<Long> userFileIds = shareItems.stream()
                .map(ShareItem::getUserFileId)
                .collect(Collectors.toList());
        
        // 6. 根据文件ID列表查询文件详情 - 获取分享的根目录文件列表
        List<UserFile> rootUserFiles = fileMapper.findUserFilesByIds(userFileIds);
        if (rootUserFiles == null || rootUserFiles.isEmpty()) {
            log.error("未找到分享的文件: {}", shareId);
            return null;
        }
        
        // 7. 如果提供了folderId，获取对应文件夹下的内容
        List<UserFile> resultFiles;
        if (folderId != null) {
            // 7.1 验证该文件夹是否属于该分享（直接分享的文件夹或者是分享文件夹的子文件夹）
            boolean isFolderInShare = false;
            
            // 直接检查是否是分享的根目录文件夹
            for (UserFile rootFile : rootUserFiles) {
                if (rootFile.getId().equals(folderId) && rootFile.getFolderType() == 1) {
                    isFolderInShare = true;
                    break;
                }
            }
            
            // 如果不是分享的根目录文件夹，需要检查是否是分享文件夹的子文件夹
            if (!isFolderInShare) {
                // 获取所有分享的文件夹ID
                List<Long> sharedFolderIds = rootUserFiles.stream()
                        .filter(file -> file.getFolderType() == 1)
                        .map(UserFile::getId)
                        .collect(Collectors.toList());
                
                if (!sharedFolderIds.isEmpty()) {
                    // 检查folderId是否是这些文件夹的子文件夹
                    for (Long sharedFolderId : sharedFolderIds) {
                        // 利用递归查询检查folderId是否是sharedFolderId的子文件夹
                        List<UserFile> childFolders = fileMapper.findAllByFilePidRecursive(sharedFolderId);
                        for (UserFile childFolder : childFolders) {
                            if (childFolder.getId().equals(folderId) && childFolder.getFolderType() == 1) {
                                isFolderInShare = true;
                                break;
                            }
                        }
                        if (isFolderInShare) {
                            break;
                        }
                    }
                }
            }
            
            if (!isFolderInShare) {
                log.error("文件夹不属于该分享: shareId={}, folderId={}", shareId, folderId);
                return null;
            }
            
            // 7.2 获取文件夹下的内容
            resultFiles = fileMapper.findByFilePid(folderId);
        } else {
            // 如果未提供folderId，则返回分享的根目录文件列表
            resultFiles = rootUserFiles;
        }
        
        // 8. 构建返回结果
        FileList fileList = new FileList();
        fileList.setList(resultFiles);
        
        // 创建分页信息
        PageMeta pageMeta = new PageMeta(resultFiles.size(), 1, resultFiles.size(), 1);
        fileList.setPageData(pageMeta);
        
        return fileList;
    }
} 