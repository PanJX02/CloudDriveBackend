package com.panjx.clouddrive.service.impl;

import com.panjx.clouddrive.mapper.FileMapper;
import com.panjx.clouddrive.mapper.ShareMapper;
import com.panjx.clouddrive.mapper.UserMapper;
import com.panjx.clouddrive.pojo.FileShare;
import com.panjx.clouddrive.pojo.Result;
import com.panjx.clouddrive.pojo.ShareItem;
import com.panjx.clouddrive.pojo.UserFile;
import com.panjx.clouddrive.pojo.request.CreateShareRequest;
import com.panjx.clouddrive.pojo.response.FileList;
import com.panjx.clouddrive.pojo.response.PageMeta;
import com.panjx.clouddrive.pojo.response.ShareListResponse;
import com.panjx.clouddrive.pojo.response.ShareResponse;
import com.panjx.clouddrive.service.ShareService;
import com.panjx.clouddrive.utils.SecurityUtil;
import com.panjx.clouddrive.utils.ShareUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
@Service
public class ShareServiceImpl implements ShareService {

    @Autowired
    private ShareMapper shareMapper;
    
    @Autowired
    private FileMapper fileMapper;

    @Autowired
    private UserMapper userMapper;

    @Override
    @Transactional
    public Result createShare(CreateShareRequest createShareRequest) {
        log.info("创建分享请求: {}", createShareRequest);
        
        // 获取当前用户ID
        Long userId = SecurityUtil.getCurrentUserId();
        if (userId == null) {
            return Result.error("用户未登录");
        }
        
        // 验证文件列表
        List<Long> userFileIds = createShareRequest.getUserFileIds();
        if (userFileIds == null || userFileIds.isEmpty()) {
            return Result.error("分享文件列表不能为空");
        }
        
        // 验证文件是否存在且属于当前用户
        List<UserFile> userFiles = new ArrayList<>();
        for (Long fileId : userFileIds) {
            UserFile userFile = fileMapper.findById(fileId);
            if (userFile == null) {
                return Result.error("文件不存在");
            }
            if (!userId.equals(userFile.getUserId())) {
                return Result.error("无权分享他人文件");
            }
            userFiles.add(userFile);
        }
        
        // 生成share_name
        String shareName = generateShareName(userFiles);
        
        // 生成提取码
        String code = ShareUtil.generateCode();
        
        // 计算过期时间
        Long expireTime = ShareUtil.calculateExpireTime(createShareRequest.getValidType());
        
        // 创建分享记录
        FileShare fileShare = new FileShare();
        fileShare.setUserId(userId);
        fileShare.setShareName(shareName);
        fileShare.setValidType(createShareRequest.getValidType());
        fileShare.setExpireTime(expireTime);
        fileShare.setShareTime(System.currentTimeMillis());
        fileShare.setCode(code);
        fileShare.setShowCount(0);
        fileShare.setIsExpired(0);
        
        // 添加分享记录（自动生成shareId并回填）
        shareMapper.addShare(fileShare);
        
        // 获取数据库自动生成的分享ID
        Long shareId = fileShare.getShareId();
        
        // 添加分享项
        for (Long fileId : userFileIds) {
            ShareItem shareItem = new ShareItem();
            shareItem.setShareId(shareId);
            shareItem.setUserFileId(fileId);
            shareMapper.addShareItem(shareItem);
        }
        
        // 生成不包含提取码的加密标识
        String encryptedShareId = ShareUtil.encryptShareId(shareId);
        
        // 生成包含提取码的加密标识
        String encryptedShareIdWithCode = ShareUtil.encryptShareIdWithCode(shareId, code);
        
        // 构建返回对象
        ShareResponse shareResponse = new ShareResponse();
        shareResponse.setShareKey(encryptedShareId);
        shareResponse.setShareKeyWithCode(encryptedShareIdWithCode);
        shareResponse.setCode(code);
        shareResponse.setShareName(shareName);
        
        return Result.success(shareResponse);
    }
    
    /**
     * 生成分享名称
     * @param userFiles 用户文件列表
     * @return 分享名称
     */
    private String generateShareName(List<UserFile> userFiles) {
        if (userFiles == null || userFiles.isEmpty()) {
            return "未知文件";
        }
        
        // 获取第一个文件/文件夹的名称
        UserFile firstFile = userFiles.get(0);
        String firstName = firstFile.getFileName();
        if (firstFile.getFolderType() == 0 && firstFile.getFileExtension() != null && !firstFile.getFileExtension().isEmpty()) {
            firstName += "." + firstFile.getFileExtension();
        }
        
        // 如果只有一个文件/文件夹，直接返回名称
        if (userFiles.size() == 1) {
            return firstName;
        }
        
        // 多个文件/文件夹，返回"第一个名称 等XX个文件（夹）"
        String suffix = "文件";
        
        // 检查是否全是文件夹，或全是文件，或混合
        boolean hasFile = false;
        boolean hasFolder = false;
        
        for (UserFile userFile : userFiles) {
            if (userFile.getFolderType() == 0) { // 文件
                hasFile = true;
            } else { // 文件夹
                hasFolder = true;
            }
            
            // 如果已经确认既有文件又有文件夹，可以提前退出循环
            if (hasFile && hasFolder) {
                break;
            }
        }
        
        if (hasFile && hasFolder) {
            // 混合文件和文件夹
            suffix = "文件（夹）";
        } else if (hasFolder) {
            // 全是文件夹
            suffix = "文件夹";
        } // 否则默认是"文件"
        
        return firstName + " 等" + userFiles.size() + "个" + suffix;
    }
    
    @Override
    @Transactional
    public Result getUserShares(boolean showAll) {
        log.info("获取用户分享列表, showAll: {}", showAll);
        
        // 获取当前用户ID
        Long userId = SecurityUtil.getCurrentUserId();
        if (userId == null) {
            return Result.error("用户未登录");
        }
        
        // 获取当前时间
        long currentTime = System.currentTimeMillis();
        
        // 查询分享列表
        List<FileShare> shares;
        if (showAll) {
            // 查询所有分享（包括已过期）
            shares = shareMapper.findSharesByUserId(userId);
        } else {
            // 只查询有效分享
            shares = shareMapper.findActiveSharesByUserId(userId);
        }
        
        // 检查并更新分享过期状态
        for (FileShare share : shares) {
            // 只检查当前标记为未过期的分享
            if (share.getIsExpired() == 0) {
                // 如果validType为3（永久有效），则不检查过期
                if (share.getValidType() != 3) {
                    // 检查过期时间
                    Long expireTime = share.getExpireTime();
                    if (expireTime != null && expireTime < currentTime) {
                        // 已过期，更新状态
                        share.setIsExpired(1);
                        shareMapper.updateExpiredStatus(share.getShareId(), 1);
                        log.info("更新分享过期状态: shareId={}, isExpired=1", share.getShareId());
                    }
                }
            }
        }
        
        // 如果不显示过期分享，则过滤掉已过期的
        if (!showAll) {
            shares = shares.stream()
                    .filter(share -> share.getIsExpired() == 0)
                    .collect(Collectors.toList());
        }
        
        // 转换为响应对象
        List<ShareListResponse> responseList = new ArrayList<>();
        for (FileShare share : shares) {
            ShareListResponse response = convertToShareListResponse(share);
            responseList.add(response);
        }
        
        return Result.success(responseList);
    }
    
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
    
    /**
     * 将FileShare对象转换为ShareListResponse响应对象
     * @param share 分享信息
     * @return 分享列表响应对象
     */
    private ShareListResponse convertToShareListResponse(FileShare share) {
        ShareListResponse response = new ShareListResponse();
        
        // 设置基本信息
        response.setShareId(share.getShareId());
        response.setShareName(share.getShareName());
        response.setShareTime(share.getShareTime());
        response.setValidType(share.getValidType());
        response.setExpireTime(share.getExpireTime());
        response.setIsExpired(share.getIsExpired());
        response.setShowCount(share.getShowCount());
        response.setCode(share.getCode());
        
        // 设置加密标识
        response.setShareKey(ShareUtil.encryptShareId(share.getShareId()));
        response.setShareKeyWithCode(ShareUtil.encryptShareIdWithCode(share.getShareId(), share.getCode()));
        
        // 查询分享项
        List<ShareItem> shareItems = shareMapper.findShareItemsByShareId(share.getShareId());
        
        // 设置文件数量
        response.setFileCount(shareItems.size());
        
        return response;
    }

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
            if (!shareFileIdSet.contains(fileId)) {
                return Result.error("存在不属于该分享的文件");
            }
        }
        
        // 9. 获取要保存的文件信息
        List<UserFile> sourceFiles = fileMapper.findUserFilesByIds(fileIds);
        if (sourceFiles == null || sourceFiles.isEmpty()) {
            return Result.error("获取分享文件信息失败");
        }
        
        // 10. 执行保存操作（复制文件）
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
        
        // 11. 返回结果
        if (successCount == 0) {
            return Result.error("所有文件保存失败");
        } else if (failedFiles.isEmpty()) {
            return Result.success("成功保存" + successCount + "个文件");
        } else {
            return Result.success("成功保存" + successCount + "个文件，失败" + failedFiles.size() + "个");
        }
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
} 
