package com.panjx.clouddrive.pojo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserFile {
    /**
     * 关联ID
     */
    private Long id;

    /**
     * 用户ID
     */
    private Long userId;

    /**
     * 文件ID，文件夹ID为null
     */
    private Long fileId;

    /**
     * 用户定义的文件名，不包含扩展名
     */
    private String fileName;

    /**
     * 文件后缀，如 pdf, doc, mp4 等
     */
    private String fileExtension;

    /**
     * MIME 类型
     */
    private String fileCategory;

    /**
     * 父目录ID，根目录为0
     */
    private Long filePid;

    /**
     * 0:文件 1:目录
     */
    private Integer folderType;

    /**
     * 0:删除 1:回收站 2:正常
     */
    private Integer deleteFlag;

    /**
     * 回收站过期时间
     */
    private Long recoveryTime;

    /**
     * 关联创建时间
     */
    private Long createTime;

    /**
     * 最后更新时间
     */
    private Long lastUpdateTime;

    // 以下是从 file 表关联的字段

    /**
     * 文件MD5值
     */
    private String fileMD5;

    /**
     * 文件SHA1值
     */
    private String fileSHA1;

    /**
     * 文件SHA256值
     */
    private String fileSHA256;

    /**
     * 关联存储配置ID（通过storage_config表获取endpoint/region/bucket）
     */
    private Integer storageId;

    /**
     * 文件大小（字节）
     */
    private Long fileSize;

    /**
     * 文件封面
     */
    private String fileCover;

    /**
     * 引用计数
     */
    private Integer referCount;

    /**
     * 1:正常 2:待删除
     */
    private Integer status;

    /**
     * 转码状态: 0-未转码 1-转码中 2-转码成功 3-转码失败
     */
    private Integer transcodeStatus;

    /**
     * 首次上传时间
     */
    private Long fileCreateTime;

    /**
     * 最后引用时间
     */
    private Long lastReferTime;
}

