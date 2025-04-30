package com.panjx.clouddrive.mapper;

import com.panjx.clouddrive.pojo.FileShare;
import com.panjx.clouddrive.pojo.ShareItem;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.util.List;

@Mapper
public interface ShareMapper {
    
    /**
     * 添加分享记录
     */
    @Insert("INSERT INTO file_share(user_id, share_name, valid_type, expire_time, share_time, code, show_count, is_expired) " +
            "VALUES(#{userId}, #{shareName}, #{validType}, #{expireTime}, #{shareTime}, #{code}, 0, 0)")
    @Options(useGeneratedKeys = true, keyProperty = "shareId")
    void addShare(FileShare fileShare);
    
    /**
     * 添加分享项
     */
    @Insert("INSERT INTO share_item(share_id, user_file_id) VALUES(#{shareId}, #{userFileId})")
    void addShareItem(ShareItem shareItem);
    
    /**
     * 根据分享ID查询分享信息
     */
    @Select("SELECT * FROM file_share WHERE share_id = #{shareId}")
    FileShare findShareById(Long shareId);
    
    /**
     * 根据分享ID查询分享项
     */
    @Select("SELECT * FROM share_item WHERE share_id = #{shareId}")
    List<ShareItem> findShareItemsByShareId(Long shareId);
    
    /**
     * 根据用户ID查询该用户的所有分享
     * @param userId 用户ID
     * @return 分享列表
     */
    @Select("SELECT * FROM file_share WHERE user_id = #{userId} ORDER BY share_time DESC")
    List<FileShare> findSharesByUserId(Long userId);
    
    /**
     * 根据用户ID查询该用户有效的分享
     * @param userId 用户ID
     * @return 有效分享列表
     */
    @Select("SELECT * FROM file_share WHERE user_id = #{userId} AND is_expired = 0 ORDER BY share_time DESC")
    List<FileShare> findActiveSharesByUserId(Long userId);
    
    /**
     * 更新分享过期状态
     * @param shareId 分享ID
     * @param isExpired 过期状态（0:有效 1:过期）
     */
    @Update("UPDATE file_share SET is_expired = #{isExpired} WHERE share_id = #{shareId}")
    void updateExpiredStatus(Long shareId, Integer isExpired);
    
    /**
     * 更新分享查看次数
     * @param shareId 分享ID
     * @param showCount 新的查看次数
     */
    @Update("UPDATE file_share SET show_count = #{showCount} WHERE share_id = #{shareId}")
    void updateShowCount(Long shareId, Integer showCount);
} 