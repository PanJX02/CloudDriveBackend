package com.panjx.clouddrive.pojo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Admin {
    /**
     * 管理员ID
     */
    private Long id;

    /**
     * 管理员名
     */
    private String adminName;

    /**
     * 密码
     */
    private String password;

    /**
     * 昵称
     */
    private String nickName;

    /**
     * 身份
     * 用于区分管理员角色或类型
     */
    private Integer identity;

    /**
     * 注册时间
     */
    private Long registerTime;

    /**
     * 最后登录时间
     */
    private Long lastLoginTime;
} 