package com.panjx.clouddrive.pojo.request;

import lombok.Data;

/**
 * 文件上传完成请求对象
 */
@Data
public class UploadCompleteRequest {
    /**
     * 文件名（不含扩展名）
     */
    private String fileName;
    
    /**
     * 文件扩展名
     */
    private String fileExtension;
    
    /**
     * 文件SHA256值
     */
    private String fileSHA256;
    
    /**
     * 文件MD5值
     */
    private String fileMD5;
    
    /**
     * 文件SHA1值
     */
    private String fileSHA1;
    
    /**
     * 父目录ID
     */
    private Long file_pid;
    
    /**
     * 文件大小（字节）
     */
    private Long fileSize;
    
    /**
     * 文件类型
     */
    private String fileCategory;
    
    /**
     * 存储桶
     */
    private String bucket;
    
    /**
     * 存储路径/对象名
     */
    private String objectKey;
} 