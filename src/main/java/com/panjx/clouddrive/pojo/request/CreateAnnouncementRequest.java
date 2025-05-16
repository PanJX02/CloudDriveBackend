package com.panjx.clouddrive.pojo.request;

import lombok.Data;

/**
 * 创建公告请求参数
 */
@Data
public class CreateAnnouncementRequest {
    /**
     * 公告标题
     */
    private String title;
    
    /**
     * 公告内容
     */
    private String content;
    
    /**
     * 过期时间，null表示永不过期
     */
    private Long expiryTime;
    
    /**
     * 重要性：1=普通，2=重要，3=紧急
     */
    private Integer importance;
    
    /**
     * 状态：0=草稿，1=已发布，2=已撤回
     */
    private Integer status;
} 