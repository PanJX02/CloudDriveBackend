package com.panjx.clouddrive.pojo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 系统公告实体类
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Announcement {
    /**
     * 公告ID
     */
    private Integer id;
    
    /**
     * 公告标题
     */
    private String title;
    
    /**
     * 公告内容
     */
    private String content;
    
    /**
     * 发布时间
     */
    private Long publishTime;
    
    /**
     * 过期时间，null表示永不过期
     */
    private Long expiryTime;
    
    /**
     * 发布者ID
     */
    private Integer publisherId;
    
    /**
     * 重要性：1=普通，2=重要，3=紧急
     */
    private Integer importance;
    
    /**
     * 状态：0=草稿，1=已发布，2=已撤回
     */
    private Integer status;
    
    /**
     * 查看次数
     */
    private Integer viewCount;
    
    /**
     * 创建时间
     */
    private Long createdAt;
    
    /**
     * 更新时间
     */
    private Long updatedAt;
} 