package com.panjx.clouddrive.service.share.impl;

import com.panjx.clouddrive.mapper.ShareMapper;
import com.panjx.clouddrive.pojo.FileShare;
import com.panjx.clouddrive.pojo.Result;
import com.panjx.clouddrive.pojo.response.ShareListResponse;
import com.panjx.clouddrive.service.share.ShareListService;
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
public class ShareListServiceImpl implements ShareListService {

    @Autowired
    private ShareMapper shareMapper;

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
        List<com.panjx.clouddrive.pojo.ShareItem> shareItems = shareMapper.findShareItemsByShareId(share.getShareId());
        
        // 设置文件数量
        response.setFileCount(shareItems.size());
        
        return response;
    }
} 