package com.panjx.clouddrive.service.share;

import com.panjx.clouddrive.pojo.Result;
import com.panjx.clouddrive.pojo.request.CreateShareRequest;

public interface ShareCreateService {
    /**
     * 创建分享
     * @param createShareRequest 创建分享请求
     * @return 创建结果
     */
    Result createShare(CreateShareRequest createShareRequest);
} 