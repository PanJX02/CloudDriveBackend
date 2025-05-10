package com.panjx.clouddrive.mapper;

import com.panjx.clouddrive.pojo.FileShare;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface FileShareMapper {
    /**
     * 统计分享记录总数
     *
     * @return 总记录数
     */
    int countFileShares();

    /**
     * 分页查询所有分享记录
     *
     * @param offset 起始位置
     * @param limit  每页数量
     * @return 分享记录列表
     */
    List<FileShare> getFileSharesByPage(@Param("offset") int offset, @Param("limit") int limit);
    
    /**
     * 更新分享信息
     *
     * @param fileShare 分享信息
     * @return 受影响的行数
     */
    int updateFileShare(FileShare fileShare);
    
    /**
     * 根据ID查询分享信息
     *
     * @param shareId 分享ID
     * @return 分享信息
     */
    FileShare getFileShareById(@Param("shareId") Long shareId);
} 