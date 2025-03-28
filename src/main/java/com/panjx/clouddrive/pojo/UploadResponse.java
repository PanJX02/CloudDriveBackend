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
     * 上传令牌，文件不存在时返回
     */
    private String uploadToken;
    
    /**
     * 创建文件已存在的响应
     */
    public static UploadResponse fileExists() {
        return new UploadResponse(true, null);
    }
    
    /**
     * 创建包含上传令牌的响应
     */
    public static UploadResponse withToken(String uploadToken) {
        return new UploadResponse(false, uploadToken);
    }
} 