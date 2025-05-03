package com.panjx.clouddrive.service.file;

import com.panjx.clouddrive.pojo.Result;

import java.util.List;

public interface FileDeleteService {
    /**
     * 批量将文件或文件夹移动到回收站（软删除）
     * @param fileIds 文件/文件夹ID列表
     * @return 操作结果
     */
    Result moveToRecycleBin(List<Long> fileIds);
} 