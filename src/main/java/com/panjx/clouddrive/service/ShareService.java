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
    
    /**
     * 获取当前用户的分享列表
     * @param showAll 是否显示全部（包括已过期），true表示显示全部，false表示只显示有效的
     * @return 分享列表
     */
    Result getUserShares(boolean showAll);
} 