package com.panjx.clouddrive.repository;

import com.panjx.clouddrive.pojo.InvitationCode;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface InvitationCodeRepository {
    
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
} 