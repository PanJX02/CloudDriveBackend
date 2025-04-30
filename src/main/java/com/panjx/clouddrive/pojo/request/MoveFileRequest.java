package com.panjx.clouddrive.pojo.request;

import lombok.Data;
import java.util.List;

@Data
public class MoveFileRequest {
    private List<Long> ids;      // 被移动的文件/文件夹ID列表
    private Long targetFolderId;  // 目标文件夹ID
} 