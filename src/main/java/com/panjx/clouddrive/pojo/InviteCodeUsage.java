package com.panjx.clouddrive.pojo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 邀请码使用记录实体类
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class InviteCodeUsage {
    /**
     * ID
     */
    private Long id;

    /**
     * 关联的邀请码ID
     */
    private Long inviteCodeId;

    /**
     * 使用者用户ID
     */
    private Long usedBy;

    /**
     * 使用时间（秒级时间戳）
     */
    private Long usedAt;

    /**
     * 使用IP地址
     */
    private String usedIp;
} 