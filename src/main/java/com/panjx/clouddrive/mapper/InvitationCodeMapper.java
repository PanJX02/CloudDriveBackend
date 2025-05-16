package com.panjx.clouddrive.mapper;

import com.panjx.clouddrive.pojo.InvitationCode;
import com.panjx.clouddrive.pojo.InviteCodeUsage;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

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
     * @param usage 使用记录
     * @return 影响的行数
     */
    int addUsageRecord(InviteCodeUsage usage);
    
    /**
     * 获取邀请码总数
     * @return 邀请码总数
     */
    int countInvitationCodes();
    
    /**
     * 分页获取邀请码列表
     * @param offset 偏移量
     * @param pageSize 每页数量
     * @return 邀请码列表
     */
    List<InvitationCode> getInvitationCodesByPage(@Param("offset") int offset, @Param("pageSize") int pageSize);

    /**
     * 更新邀请码信息
     * @param invitationCode 邀请码对象
     * @return 影响的行数
     */
    int updateInvitationCode(InvitationCode invitationCode);

    /**
     * 创建邀请码
     * @param invitationCode 邀请码对象
     * @return 影响的行数
     */
    int insertInvitationCode(InvitationCode invitationCode);

    /**
     * 根据ID删除邀请码
     * @param id 邀请码ID
     * @return 影响的行数
     */
    int deleteInvitationCodeById(Long id);
} 