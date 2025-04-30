package com.panjx.clouddrive.pojo.request;

import lombok.Data;
import java.util.List;

/**
 * 批量操作文件请求对象
 */
@Data
public class FileIdsRequest {
    private List<Long> ids;      // 文件/文件夹ID列表
}