package com.panjx.clouddrive.pojo;

import lombok.Data;

/**
 * 文件分享信息实体类
 */
@Data
public class FileShare {
    /**
     * 分享ID
     */
    private Long shareId;
    
    /**
     * 发起分享的用户ID
     */
    private Long userId;
    
    /**
     * 有效类型 0:1天 1:7天 2:30天 3:永久
     */
    private Integer validType;
    
    /**
     * 过期时间
     */
    private Long expireTime;
    
    /**
     * 分享时间
     */
    private Long shareTime;
    
    /**
     * 提取码
     */
    private String code;
    
    /**
     * 浏览次数
     */
    private Integer showCount;
    
    /**
     * 过期状态（0:有效 1:过期）
     */
    private Integer isExpired;
} 