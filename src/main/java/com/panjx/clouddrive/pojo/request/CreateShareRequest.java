package com.panjx.clouddrive.pojo.request;

import lombok.Data;

import java.util.List;

/**
 * 创建分享请求对象
 */
@Data
public class CreateShareRequest {
    /**
     * 要分享的文件ID列表
     */
    private List<Long> userFileIds;
    
    /**
     * 有效类型 0:1天 1:7天 2:30天 3:永久
     */
    private Integer validType;
} 