package com.panjx.clouddrive.service;

import com.panjx.clouddrive.pojo.InvitationCode;

public interface InvitationCodeService {
    
    /**
     * 验证邀请码
     * @param inviteCode 邀请码字符串
     * @return 邀请码对象，如果有效；否则返回null
     */
    InvitationCode validateInviteCode(String inviteCode);
    
    /**
     * 记录邀请码使用
     * @param invitationCode 邀请码对象
     * @param userId 使用者用户ID
     * @param ip 使用者IP地址
     * @return 是否成功记录
     */
    boolean recordInviteCodeUsage(InvitationCode invitationCode, Long userId, String ip);
} 