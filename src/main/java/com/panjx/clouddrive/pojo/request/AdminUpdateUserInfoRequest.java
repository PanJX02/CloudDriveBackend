package com.panjx.clouddrive.pojo.request;

import lombok.Data;

/**
 * 管理员修改用户信息请求
 */
@Data
public class AdminUpdateUserInfoRequest {
    /**
     * 用户ID
     */
    private Long userId;
    
    /**
     * 用户名
     */
    private String username;
    
    /**
     * 昵称
     */
    private String nickname;

    /**
     * 邮箱
     */
    private String email;
    
    /**
     * 头像
     */
    private String avatar;
    
    /**
     * 密码（可选，为空则不修改密码）
     */
    private String password;
    
    /**
     * 用户身份
     */
    private Integer identity;
    
    /**
     * 用户状态 0:禁用 1:正常
     */
    private Integer status;
    
    /**
     * 总空间（字节）
     */
    private Long totalSpace;
} 