package com.panjx.clouddrive.service.admin;

import com.panjx.clouddrive.pojo.Result;
import com.panjx.clouddrive.pojo.request.PageRequest;
import com.panjx.clouddrive.pojo.request.UpdateFileRequest;

public interface AdminFileService {
    /**
     * 管理员获取所有文件信息（分页）
     * @param pageRequest 分页请求参数
     * @return 文件列表结果
     */
    Result getAllFiles(PageRequest pageRequest);
    
    /**
     * 管理员修改文件信息
     * @param updateFileRequest 文件信息修改请求
     * @return 修改结果
     */
    Result updateFileInfo(UpdateFileRequest updateFileRequest);
} 