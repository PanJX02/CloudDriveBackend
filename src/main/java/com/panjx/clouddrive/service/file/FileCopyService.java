package com.panjx.clouddrive.service.file;

import com.panjx.clouddrive.pojo.Result;
import com.panjx.clouddrive.pojo.request.CopyFileRequest;

public interface FileCopyService {
    /**
     * 复制文件或文件夹
     * @param copyFileRequest 复制请求
     * @return 操作结果
     */
    Result copyFile(CopyFileRequest copyFileRequest);
} 