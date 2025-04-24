package com.panjx.clouddrive.service;

import com.panjx.clouddrive.pojo.Announcement;
import com.panjx.clouddrive.pojo.Result;

import java.util.List;

public interface AnnouncementService {
    
    /**
     * 获取所有有效公告
     * @return 结果对象
     */
    Result getAllAnnouncements();
    
    /**
     * 获取公告详情
     * @param id 公告ID
     * @return 结果对象
     */
    Result getAnnouncementDetail(Integer id);
    
    /**
     * 获取指定重要性的公告
     * @param importance 重要性级别
     * @return 结果对象
     */
    Result getAnnouncementsByImportance(Integer importance);
    
    /**
     * 获取最新的N条公告
     * @param limit 限制条数
     * @return 结果对象
     */
    Result getLatestAnnouncements(Integer limit);
} 