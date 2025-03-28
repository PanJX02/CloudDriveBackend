package com.panjx.clouddrive.pojo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 对象存储位置配置实体类
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Storage {
    /**
     * 存储配置唯一标识
     */
    private Integer storageId;

    /**
     * 对象存储服务地址
     * 例如：oss-cn-beijing.aliyuncs.com
     */
    private String endpoint;

    /**
     * 存储区域标识
     * 例如：cn-beijing
     */
    private String region;

    /**
     * 存储桶名称
     * 需符合云服务商命名规则，如 my-app-files
     */
    private String bucket;

    /**
     * 是否为默认存储位置
     * 0:否 1:是，每个用户/系统只能有一个默认
     */
    private Boolean isDefault;

    /**
     * 配置描述
     * 例如：北京高可用存储/东京备份节点
     */
    private String description;
} 