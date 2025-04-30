package com.panjx.clouddrive.service.share;

import com.panjx.clouddrive.pojo.Result;

public interface ShareListService {
    /**
     * 获取用户分享列表
     * @param showAll 是否显示所有分享（包括已过期）
     * @return 用户分享列表
     */
    Result getUserShares(boolean showAll);
} 