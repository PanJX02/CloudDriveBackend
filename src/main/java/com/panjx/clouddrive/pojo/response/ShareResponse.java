package com.panjx.clouddrive.pojo.response;

import lombok.Data;

/**
 * 分享响应对象
 */
@Data
public class ShareResponse {
    /**
     * 分享标识（不包含提取码的加密后shareId，需要单独提供提取码）
     */
    private String shareKey;
    
    /**
     * 带提取码的分享标识（已将提取码编码到标识中）
     */
    private String shareKeyWithCode;
    
    /**
     * 提取码
     */
    private String code;
    
    /**
     * 分享名称
     */
    private String shareName;
} 