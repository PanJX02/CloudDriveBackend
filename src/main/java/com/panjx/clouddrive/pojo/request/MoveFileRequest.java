package com.panjx.clouddrive.pojo.request;

import lombok.Data;

@Data
public class MoveFileRequest {
    private Long id;      // 被移动的文件/文件夹ID
    private Long targetFolderId;  // 目标文件夹ID
} 