package com.panjx.clouddrive.pojo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 邀请码实体类
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class InvitationCode {
    /**
     * 邀请码ID
     */
    private Long id;

    /**
     * 邀请码字符串
     */
    private String inviteCode;

    /**
     * 创建者用户ID
     */
    private Long userId;

    /**
     * 创建者管理员ID
     */
    private Long adminId;

    /**
     * 创建时间（秒级时间戳）
     */
    private Long createdAt;

    /**
     * 过期时间（秒级时间戳）
     * 0表示永不过期
     */
    private Long expirationTime;

    /**
     * 状态
     * 0=disabled, 1=active
     */
    private Integer status;

    /**
     * 当前已使用次数
     */
    private Integer usageCount;

    /**
     * 最大使用次数
     * 0表示无限次
     */
    private Integer maxUsage;

    /**
     * 邀请码身份类型
     * 0=内部测试 1=普通用户, 2=VIP1, 3=VIP2, 4=其他
     */
    private Integer identity;
} 