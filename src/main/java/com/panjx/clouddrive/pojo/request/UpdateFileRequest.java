package com.panjx.clouddrive.pojo.request;

import lombok.Data;

@Data
public class UpdateFileRequest {
    // 文件ID（不可修改，仅用于识别）
    private Long fileId;
    
    // 文件MD5值
    private String fileMd5;
    
    // 文件SHA1值
    private String fileSha1;
    
    // 文件SHA256值
    private String fileSha256;
    
    // MIME 类型
    private String fileCategory;
    
    // 关联存储配置ID
    private Integer storageId;
    
    // 文件大小（字节）
    private Long fileSize;
    
    // 文件封面
    private String fileCover;
    
    // 引用计数
    private Integer referCount;
    
    // 文件状态 1:正常 2:待删除
    private Integer status;
} 