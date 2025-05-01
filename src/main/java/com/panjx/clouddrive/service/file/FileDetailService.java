package com.panjx.clouddrive.service.file;

import com.panjx.clouddrive.pojo.Result;

import java.util.List;

public interface FileDetailService {

    /**
     * 批量获取文件或文件夹的详细信息
     * @param fileIds 文件/文件夹ID列表
     * @return 详细信息结果
     */
    Result getFileDetails(List<Long> fileIds);
} 