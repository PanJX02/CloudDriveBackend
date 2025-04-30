package com.panjx.clouddrive.service.file;

import com.panjx.clouddrive.pojo.Result;

public interface FileDetailService {
    /**
     * 获取文件或文件夹的详细信息
     * @param fileId 文件/文件夹ID
     * @return 详细信息结果
     */
    Result getFileDetail(Long fileId);
} 