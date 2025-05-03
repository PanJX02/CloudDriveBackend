package com.panjx.clouddrive.service.share.impl;

import com.panjx.clouddrive.mapper.ShareMapper;
import com.panjx.clouddrive.pojo.FileShare;
import com.panjx.clouddrive.pojo.Result;
import com.panjx.clouddrive.service.share.ShareCancelService;
import com.panjx.clouddrive.utils.SecurityUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
public class ShareCancelServiceImpl implements ShareCancelService {

    @Autowired
    private ShareMapper shareMapper;

    /**
     * 取消分享
     * @param shareId 分享ID
     * @param code 提取码(可选，用于验证)
     * @return 取消结果
     */
    @Override
    @Transactional
    public Result cancelShare(Long shareId, String code) {
        log.info("取消分享, shareId: {}", shareId);
        
        // 1. 检查参数
        if (shareId == null) {
            return Result.error("分享ID不能为空");
        }
        
        // 2. 获取当前用户ID
        Long userId = SecurityUtil.getCurrentUserId();
        if (userId == null) {
            return Result.error("用户未登录");
        }
        
        // 3. 检查分享是否存在
        FileShare share = shareMapper.findShareById(shareId);
        if (share == null) {
            return Result.error("分享不存在或已被取消");
        }
        
        // 4. 校验当前用户是否有权限取消该分享（只能取消自己的分享）
        if (!share.getUserId().equals(userId)) {
            return Result.error("无权限取消该分享");
        }
        
        // 5. 检查提取码（如果需要）
        if (code != null && !code.isEmpty()) {
            if (!code.equals(share.getCode())) {
                return Result.error("提取码错误");
            }
        }
        
        // 6. 删除分享项
        shareMapper.deleteShareItemsByShareId(shareId);
        
        // 7. 删除分享记录
        shareMapper.deleteShareById(shareId);
        
        log.info("成功取消分享, shareId: {}", shareId);
        return Result.success("已取消分享");
    }
} 