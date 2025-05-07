package com.panjx.clouddrive.service.impl;

import com.panjx.clouddrive.mapper.InvitationCodeMapper;
import com.panjx.clouddrive.pojo.InvitationCode;
import com.panjx.clouddrive.pojo.InviteCodeUsage;
import com.panjx.clouddrive.service.InvitationCodeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class InvitationCodeServiceImpl implements InvitationCodeService {

    @Autowired
    private InvitationCodeMapper invitationCodeMapper;

    @Override
    public InvitationCode validateInviteCode(String inviteCode) {
        if (inviteCode == null || inviteCode.trim().isEmpty()) {
            return null;
        }

        // 查询邀请码
        InvitationCode invitationCode = invitationCodeMapper.findByCode(inviteCode);
        if (invitationCode == null) {
            return null;
        }

        // 检查邀请码状态
        if (invitationCode.getStatus() != 1) {
            return null; // 邀请码已禁用
        }

        // 检查是否过期
        long currentTime = System.currentTimeMillis() / 1000; // 转换为秒级时间戳
        if (invitationCode.getExpirationTime() > 0 && currentTime > invitationCode.getExpirationTime()) {
            return null; // 邀请码已过期
        }

        // 检查使用次数是否达到上限
        if (invitationCode.getMaxUsage() > 0 && invitationCode.getUsageCount() >= invitationCode.getMaxUsage()) {
            return null; // 邀请码使用次数已达上限
        }

        return invitationCode;
    }

    @Override
    @Transactional
    public boolean recordInviteCodeUsage(InvitationCode invitationCode, Long userId, String ip) {
        if (invitationCode == null || userId == null) {
            return false;
        }

        try {
            // 增加邀请码使用次数
            invitationCodeMapper.incrementUsageCount(invitationCode.getId());

            // 记录邀请码使用情况
            InviteCodeUsage usage = new InviteCodeUsage();
            usage.setInviteCodeId(invitationCode.getId());
            usage.setUsedBy(userId);
            usage.setUsedAt(System.currentTimeMillis() / 1000); // 秒级时间戳
            usage.setUsedIp(ip);
            
            invitationCodeMapper.addUsageRecord(usage);
            
            return true;
        } catch (Exception e) {
            // 记录日志
            e.printStackTrace();
            return false;
        }
    }
} 