package com.panjx.clouddrive.mapper;

import com.panjx.clouddrive.pojo.InvitationCode;
import com.panjx.clouddrive.pojo.InviteCodeUsage;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface InvitationCodeMapper {
    /**
     * 根据邀请码字符串查询邀请码
     * @param inviteCode 邀请码字符串
     * @return 邀请码对象
     */
    InvitationCode findByCode(String inviteCode);
    
    /**
     * 增加邀请码使用次数
     * @param id 邀请码ID
     * @return 影响的行数
     */
    int incrementUsageCount(Long id);
    
    /**
     * 添加邀请码使用记录
     * @param inviteCodeUsage 邀请码使用记录
     */
    void addUsageRecord(InviteCodeUsage inviteCodeUsage);
} 