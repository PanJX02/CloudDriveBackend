package com.panjx.clouddrive.pojo.request;

import lombok.Data;

/**
 * 取消分享请求
 */
@Data
public class CancelShareRequest {
    /**
     * 分享的加密标识
     */
    private String shareKey;
    
    /**
     * 提取码(可选，当shareKey不包含提取码时需要单独提供)
     */
    private String code;
} 