package com.panjx.clouddrive.service.share;

import com.panjx.clouddrive.pojo.Result;
import com.panjx.clouddrive.pojo.request.CreateShareRequest;
import com.panjx.clouddrive.pojo.response.FileList;

import java.util.List;

/**
 * 文件分享服务接口
 */
public interface ShareService {
    
    /**
     * 创建分享
     * @param createShareRequest 创建分享请求
     * @return 创建结果
     */
    Result createShare(CreateShareRequest createShareRequest);
    
    /**
     * 获取当前用户的分享列表
     * @param showAll 是否显示全部（包括已过期），true表示显示全部，false表示只显示有效的
     * @return 分享列表
     */
    Result getUserShares(boolean showAll);
    
    /**
     * 获取分享的文件列表
     * @param shareId 分享ID
     * @param code 提取码(可选)
     * @param folderId 文件夹ID(可选)，如果提供则获取该文件夹下的文件，否则获取分享根目录文件
     * @return 文件列表
     */
    FileList getShareFiles(Long shareId, String code, Long folderId);
    
    /**
     * 保存分享的文件到自己的云盘
     * @param shareId 分享ID
     * @param code 提取码
     * @param fileIds 要保存的文件ID列表
     * @param targetFolderId 目标文件夹ID (保存到的位置)
     * @return 保存结果
     */
    Result saveShareFiles(Long shareId, String code, List<Long> fileIds, Long targetFolderId);
} 