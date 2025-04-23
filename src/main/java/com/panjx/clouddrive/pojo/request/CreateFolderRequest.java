package com.panjx.clouddrive.pojo.request;

import lombok.Data;

@Data
public class CreateFolderRequest {
    /**
     * 文件夹名称
     */
    private String folderName;
    
    /**
     * 父文件夹ID，如果不指定则默认为根目录(0)
     */
    private Long parentId = 0L;
} 