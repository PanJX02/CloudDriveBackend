package com.panjx.clouddrive.service.share;

import com.panjx.clouddrive.pojo.Result;

/**
 * 取消分享服务接口
 */
public interface ShareCancelService {
    /**
     * 取消分享
     * @param shareId 分享ID
     * @param code 提取码(可选，用于验证)
     * @return 取消结果
     */
    Result cancelShare(Long shareId, String code);
} 