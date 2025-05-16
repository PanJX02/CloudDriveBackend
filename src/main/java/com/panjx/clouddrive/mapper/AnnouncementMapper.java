package com.panjx.clouddrive.mapper;

import com.panjx.clouddrive.pojo.Announcement;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface AnnouncementMapper {
    
    /**
     * 获取所有有效的公告
     * @return 公告列表
     */
    List<Announcement> findAllValid();
    
    /**
     * 根据ID查询公告
     * @param id 公告ID
     * @return 公告信息
     */
    Announcement findById(Integer id);
    
    /**
     * 增加公告的查看次数
     * @param id 公告ID
     */
    void increaseViewCount(Integer id);
    
    /**
     * 根据重要性获取公告
     * @param importance 重要性级别
     * @return 公告列表
     */
    List<Announcement> findByImportance(Integer importance);
    
    /**
     * 获取最新发布的N条公告
     * @param limit 条数限制
     * @return 公告列表
     */
    List<Announcement> findLatest(Integer limit);
    
    /**
     * 获取公告总数
     * @return 公告总数
     */
    int countAll();
    
    /**
     * 分页获取公告列表
     * @param offset 偏移量
     * @param limit 条数限制
     * @return 公告列表
     */
    List<Announcement> findAllByPage(int offset, int limit);
    
    /**
     * 更新公告信息
     * @param announcement 公告信息
     * @return 受影响的行数
     */
    int updateAnnouncement(Announcement announcement);
    
    /**
     * 插入公告
     * @param announcement 公告信息
     * @return 受影响的行数
     */
    int insertAnnouncement(Announcement announcement);
} 