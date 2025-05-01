package com.panjx.clouddrive.service.file;

import com.panjx.clouddrive.pojo.Result;
import com.panjx.clouddrive.pojo.request.RenameFileRequest;

public interface FileRenameService {
    /**
     * 重命名文件或文件夹
     * @param renameFileRequest 重命名请求参数
     * @return 操作结果
     */
    Result renameFile(RenameFileRequest renameFileRequest);
} 