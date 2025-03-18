package com.panjx.clouddrive.pojo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class User {
    /**
     * 用户ID
     */
    private Long userId;

    /**
     * 用户名
     */
    private String userName;

    /**
     * 昵称
     */
    private String nickName;

    /**
     * 邮箱
     */
    private String email;

    /**
     * 身份
     * 用于区分用户角色或类型
     */
    private Integer identity;

    /**
     * QQ头像链接
     */
    private String avatar;

    /**
     * 密码
     */
    private String password;

    /**
     * 注册时间
     */
    private Long joinTime;

    /**
     * 最后登录时间
     */
    private Long lastLoginTime;

    /**
     * 用户状态
     * 0:禁用 1:正常
     */
    private Integer status;

    /**
     * 已用空间（字节）
     */
    private Long useSpace;

    /**
     * 总空间（字节）
     */
    private Long totalSpace;

    /**
     * 计算剩余可用空间（字节）
     * @return 剩余可用空间
     */
    public Long getAvailableSpace() {
        if (totalSpace == null || useSpace == null) {
            return 0L;
        }
        return totalSpace - useSpace;
    }

    /**
     * 判断用户状态是否正常
     * @return 如果状态正常返回true，否则返回false
     */
    public boolean isStatusNormal() {
        return status != null && status == 1;
    }
}