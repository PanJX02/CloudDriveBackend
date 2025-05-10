package com.panjx.clouddrive.pojo.request;

import lombok.Data;

@Data
public class UpdateShareRequest {
    /**
     * 分享ID（不可修改，用于定位记录）
     */
    private Long shareId;
    
    /**
     * 分享名称
     */
    private String shareName;
    
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