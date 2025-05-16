package com.panjx.clouddrive.service;

import com.panjx.clouddrive.pojo.InvitationCode;
import com.panjx.clouddrive.pojo.response.InvitationCodeList;
import com.panjx.clouddrive.pojo.Result;
import com.panjx.clouddrive.pojo.request.UpdateInvitationCodeRequest;
import com.panjx.clouddrive.pojo.request.CreateInvitationCodeRequest;

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

    /**
     * 获取所有邀请码（带分页）
     * @param page 页码
     * @param pageSize 每页大小
     * @return 邀请码列表及分页信息
     */
    InvitationCodeList getAllInvitationCodes(int page, int pageSize);

    /**
     * 更新邀请码信息
     * @param request 更新邀请码请求
     * @return 操作结果
     */
    Result updateInvitationCode(UpdateInvitationCodeRequest request);

    /**
     * 创建邀请码
     * @param request 创建邀请码请求
     * @return 操作结果
     */
    Result createInvitationCode(CreateInvitationCodeRequest request);

    /**
     * 根据ID删除邀请码
     * @param id 邀请码ID
     * @return 操作结果
     */
    Result deleteInvitationCode(Long id);
} 