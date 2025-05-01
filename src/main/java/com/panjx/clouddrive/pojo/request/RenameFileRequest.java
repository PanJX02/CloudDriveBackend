package com.panjx.clouddrive.pojo.request;

import lombok.Data;

@Data
public class RenameFileRequest {
    /**
     * 需要重命名的文件/文件夹ID
     */
    private Long id;
    
    /**
     * 新的文件/文件夹名称
     */
    private String newFileName;
} 