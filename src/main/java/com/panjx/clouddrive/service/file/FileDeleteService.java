package com.panjx.clouddrive.service.file;

import com.panjx.clouddrive.pojo.Result;

import java.util.List;

public interface FileDeleteService {
    /**
     * 删除文件或文件夹
     * @param fileId 文件/文件夹ID
     * @return 操作结果
     */
    Result deleteFile(Long fileId);
    
    /**
     * 批量删除文件或文件夹
     * @param fileIds 文件/文件夹ID列表
     * @return 操作结果
     */
    Result deleteFiles(List<Long> fileIds);
} 