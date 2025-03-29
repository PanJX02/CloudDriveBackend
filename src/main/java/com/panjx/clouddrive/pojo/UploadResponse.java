package com.panjx.clouddrive.pojo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 文件上传响应对象
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class UploadResponse {
    
    /**
     * 文件是否已存在
     */
    private Boolean fileExists;

    /**
     * 存储配置唯一标识
     */
    private int storageId;

    /**
     * 上传域名，文件不存在时返回
     */
    private String[] domain;

    /**
     * 上传令牌，文件不存在时返回
     */
    private String uploadToken;



    /**
     * 创建文件已存在的响应
     */
    public static UploadResponse fileExists() {
        return new UploadResponse(true,0, null,null);
    }
    
    /**
     * 创建包含上传令牌的响应
     */
    public static UploadResponse withToken(int storageId,String[] domain,String uploadToken) {
        return new UploadResponse(false,storageId, domain, uploadToken);
    }
} 