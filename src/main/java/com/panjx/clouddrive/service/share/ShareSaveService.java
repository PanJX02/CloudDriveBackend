package com.panjx.clouddrive.service.share;

import com.panjx.clouddrive.pojo.Result;

import java.util.List;

public interface ShareSaveService {
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