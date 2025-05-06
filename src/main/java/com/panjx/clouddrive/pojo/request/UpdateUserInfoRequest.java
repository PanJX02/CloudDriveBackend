package com.panjx.clouddrive.pojo.request;

import lombok.Data;

/**
 * 修改用户信息请求
 */
@Data
public class UpdateUserInfoRequest {
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
} 