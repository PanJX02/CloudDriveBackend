package com.panjx.clouddrive.service.file;

import com.panjx.clouddrive.pojo.Result;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface FileFavoriteService {

    /**
     * 收藏文件
     * @param userFileId 用户文件ID
     * @return 操作结果
     */
    Result favoriteFile(Long userFileId);
    
    /**
     * 批量收藏文件
     * @param userFileIds 用户文件ID列表
     * @return 操作结果
     */
    Result favoriteFiles(List<Long> userFileIds);
    
    /**
     * 取消收藏文件
     * @param userFileId 用户文件ID
     * @return 操作结果
     */
    Result unfavoriteFile(Long userFileId);
    
    /**
     * 批量取消收藏文件
     * @param userFileIds 用户文件ID列表
     * @return 操作结果
     */
    Result unfavoriteFiles(List<Long> userFileIds);
    
    /**
     * 获取收藏的文件列表
     * @return 收藏的文件列表
     */
    Result getFavoriteFiles();
} 