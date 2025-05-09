package com.panjx.clouddrive.service;

import com.panjx.clouddrive.pojo.Result;
import com.panjx.clouddrive.pojo.request.PageRequest;

public interface AdminFileService {
    /**
     * 管理员获取所有文件信息（分页）
     * @param pageRequest 分页请求参数
     * @return 文件列表结果
     */
    Result getAllFiles(PageRequest pageRequest);
} 