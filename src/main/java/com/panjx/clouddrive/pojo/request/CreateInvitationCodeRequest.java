package com.panjx.clouddrive.pojo.request;

import lombok.Data;
import jakarta.validation.constraints.Pattern;

@Data
public class CreateInvitationCodeRequest {
    /**
     * 邀请码字符串（可选，不填则自动生成）
     * 只能包含数字和字母，最长16位
     */
    @Pattern(regexp = "^[a-zA-Z0-9]{1,16}$", message = "邀请码格式不正确，只能包含数字和字母，且最长16位")
    private String inviteCode;
    
    /**
     * 过期时间（秒级时间戳），0表示永不过期
     */
    private Long expirationTime;
    
    /**
     * 状态：0=disabled, 1=active，默认为1
     */
    private Integer status = 1;
    
    /**
     * 最大使用次数，0表示无限次
     */
    private Integer maxUsage = 0;
    
    /**
     * 邀请码身份类型：0=内部测试 1=普通用户, 2=VIP1, 3=VIP2, 4=其他
     */
    private Integer identity = 1;
} 