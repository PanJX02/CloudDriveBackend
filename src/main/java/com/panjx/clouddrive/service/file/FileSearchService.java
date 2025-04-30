package com.panjx.clouddrive.service.file;

import com.panjx.clouddrive.pojo.Result;
import com.panjx.clouddrive.pojo.request.FileSearchRequest;

public interface FileSearchService {
    /**
     * 搜索文件
     * @param searchRequest 搜索请求参数
     * @return 搜索结果
     */
    Result searchFiles(FileSearchRequest searchRequest);
} 