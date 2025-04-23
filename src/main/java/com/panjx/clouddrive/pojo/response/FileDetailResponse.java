package com.panjx.clouddrive.pojo.response;

import lombok.Data;

@Data
public class FileDetailResponse {
    /**
     * 文件ID
     */
    private Long id;
    
    /**
     * 文件名
     */
    private String fileName;
    
    /**
     * 文件扩展名
     */
    private String fileExtension;
    
    /**
     * 文件分类
     */
    private String fileCategory;
    
    /**
     * 文件大小（字节）
     */
    private Long fileSize;
    
    /**
     * 文件类型（0：文件，1：文件夹）
     */
    private Integer folderType;
    
    /**
     * 文件所在路径
     */
    private String filePath;
    
    /**
     * 创建时间
     */
    private Long createTime;
    
    /**
     * 最后更新时间
     */
    private Long lastUpdateTime;
    
    /**
     * 子文件数量（仅文件夹有效）
     */
    private Integer fileCount;
    
    /**
     * 子文件夹数量（仅文件夹有效）
     */
    private Integer folderCount;
    
    /**
     * 预览URL（仅特定文件类型有效）
     */
    private String previewUrl;
    
    /**
     * 是否已收藏
     */
    private Boolean favoriteFlag;
} 