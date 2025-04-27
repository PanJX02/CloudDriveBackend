package com.panjx.clouddrive.service;

import com.panjx.clouddrive.pojo.Result;
import com.panjx.clouddrive.pojo.request.CreateShareRequest;

/**
 * 文件分享服务接口
 */
public interface ShareService {
    
    /**
     * 创建分享
     * @param createShareRequest 创建分享请求
     * @return 创建结果
     */
    Result createShare(CreateShareRequest createShareRequest);
} 