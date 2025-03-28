package com.panjx.clouddrive.pojo;

import lombok.Data;

/**
 * 文件实体类
 */
@Data
public class File {
    /**
     * 文件唯一ID
     */
    private Long id;

    /**
     * 文件MD5值
     */
    private String fileMd5;

    /**
     * 文件SHA1值
     */
    private String fileSha1;

    /**
     * 文件SHA256值
     */
    private String fileSha256;

    /**
     * 关联存储配置ID（通过storage_config表获取endpoint/region/bucket）
     */
    private Integer configId;

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
    private Boolean status;

    /**
     * 转码状态: 0-未转码 1-转码中 2-转码成功 3-转码失败
     */
    private Integer transcodeStatus;

    /**
     * 首次上传时间
     */
    private Long createTime;

    /**
     * 最后引用时间
     */
    private Long lastReferTime;
}
