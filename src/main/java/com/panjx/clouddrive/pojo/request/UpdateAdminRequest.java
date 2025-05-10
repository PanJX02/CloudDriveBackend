package com.panjx.clouddrive.pojo.request;

import lombok.Data;

@Data
public class UpdateAdminRequest {
    /**
     * 管理员ID（不可修改）
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
     */
    private Integer identity;
} 