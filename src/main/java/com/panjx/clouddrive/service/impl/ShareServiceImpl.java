package com.panjx.clouddrive.service.impl;

import com.panjx.clouddrive.mapper.FileMapper;
import com.panjx.clouddrive.mapper.ShareMapper;
import com.panjx.clouddrive.pojo.FileShare;
import com.panjx.clouddrive.pojo.Result;
import com.panjx.clouddrive.pojo.ShareItem;
import com.panjx.clouddrive.pojo.UserFile;
import com.panjx.clouddrive.pojo.request.CreateShareRequest;
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
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
public class ShareServiceImpl implements ShareService {

    @Autowired
    private ShareMapper shareMapper;
    
    @Autowired
    private FileMapper fileMapper;

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
} 
