package com.panjx.clouddrive.mapper;

import com.panjx.clouddrive.pojo.FileShare;
import com.panjx.clouddrive.pojo.ShareItem;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface ShareMapper {
    
    /**
     * 添加分享记录
     */
    @Insert("INSERT INTO file_share(user_id, valid_type, expire_time, share_time, code, show_count, is_expired) " +
            "VALUES(#{userId}, #{validType}, #{expireTime}, #{shareTime}, #{code}, 0, 0)")
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
} 