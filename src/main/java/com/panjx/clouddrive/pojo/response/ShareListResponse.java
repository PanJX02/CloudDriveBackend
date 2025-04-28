package com.panjx.clouddrive.pojo.response;

import lombok.Data;

import java.util.List;
import java.util.Map;

/**
 * 分享列表响应对象
 */
@Data
public class ShareListResponse {
    /**
     * 分享ID
     */
    private Long shareId;
    
    /**
     * 分享名称
     */
    private String shareName;
    
    /**
     * 分享的文件数量
     */
    private Integer fileCount;
    
    /**
     * 分享时间
     */
    private Long shareTime;
    
    /**
     * 有效类型 0:1天 1:7天 2:30天 3:永久
     */
    private Integer validType;
    
    /**
     * 过期时间
     */
    private Long expireTime;
    
    /**
     * 是否过期 0:有效 1:过期
     */
    private Integer isExpired;
    
    /**
     * 访问次数
     */
    private Integer showCount;
    
    /**
     * 提取码
     */
    private String code;
    
    /**
     * 不包含提取码的分享标识
     */
    private String shareKey;
    
    /**
     * 包含提取码的分享标识
     */
    private String shareKeyWithCode;
} 