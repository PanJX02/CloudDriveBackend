package com.panjx.clouddrive.service.file;

import com.panjx.clouddrive.pojo.Result;

public interface FileDeleteService {
    /**
     * 删除文件或文件夹
     * @param fileId 文件/文件夹ID
     * @return 操作结果
     */
    Result deleteFile(Long fileId);
} 