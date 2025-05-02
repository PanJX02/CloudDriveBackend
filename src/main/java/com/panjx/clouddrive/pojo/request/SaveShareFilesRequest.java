package com.panjx.clouddrive.pojo.request;

import lombok.Data;

import java.util.List;

/**
 * 分享文件转存请求
 */
@Data
public class SaveShareFilesRequest {
    /**
     * 要保存的文件ID列表
     */
    private List<Long> ids;
    
    /**
     * 目标文件夹ID (保存到的位置)
     */
    private Long targetFolderId;
    
    /**
     * 分享的加密标识
     */
    private String shareKey;
    
    /**
     * 提取码(当shareKey不包含提取码时需要单独提供)
     */
    private String code;
} 