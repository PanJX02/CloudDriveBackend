package com.panjx.clouddrive.service.file;

import com.panjx.clouddrive.pojo.Result;

import java.util.List;

/**
 * 回收站服务接口
 */
public interface RecycleBinService {
    
    /**
     * 获取回收站文件列表
     * @return 回收站文件列表
     */
    Result getRecycleBinFiles();
    
    /**
     * 恢复回收站中的文件或文件夹
     * @param fileIds 文件/文件夹ID列表
     * @return 操作结果
     */
    Result restoreFiles(List<Long> fileIds);
    
    /**
     * 永久删除回收站中的文件或文件夹
     * @param fileIds 文件/文件夹ID列表
     * @return 操作结果
     */
    Result deleteFilesForever(List<Long> fileIds);
    
    /**
     * 清空回收站
     * @return 操作结果
     */
    Result clearRecycleBin();
} 