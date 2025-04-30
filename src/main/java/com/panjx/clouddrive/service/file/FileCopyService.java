package com.panjx.clouddrive.service.file;

import com.panjx.clouddrive.pojo.Result;
import com.panjx.clouddrive.pojo.request.CopyFileRequest;

public interface FileCopyService {
    /**
     * 批量复制文件或文件夹
     * @param copyFileRequest 包含文件/文件夹ID列表和目标文件夹ID的复制请求
     * @return 操作结果
     */
    Result copyFile(CopyFileRequest copyFileRequest);
} 