package com.panjx.clouddrive.service.share;

import com.panjx.clouddrive.pojo.response.FileList;

public interface ShareDetailService {
    /**
     * 获取分享的文件列表
     * @param shareId 分享ID
     * @param code 提取码
     * @param folderId 文件夹ID
     * @return 文件列表
     */
    FileList getShareFiles(Long shareId, String code, Long folderId);
} 