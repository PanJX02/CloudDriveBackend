package com.panjx.clouddrive.pojo.request;

import lombok.Data;

@Data
public class FileSearchRequest {
    /**
     * 搜索关键词，同时搜索文件名和扩展名
     */
    private String keyword;
    
    /**
     * 搜索的文件夹ID
     */
    private Long folderId;
} 