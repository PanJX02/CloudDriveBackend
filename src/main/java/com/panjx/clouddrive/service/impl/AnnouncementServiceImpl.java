package com.panjx.clouddrive.service.impl;

import com.panjx.clouddrive.mapper.AnnouncementMapper;
import com.panjx.clouddrive.pojo.Announcement;
import com.panjx.clouddrive.pojo.Result;
import com.panjx.clouddrive.service.AnnouncementService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AnnouncementServiceImpl implements AnnouncementService {

    @Autowired
    private AnnouncementMapper announcementMapper;

    @Override
    public Result getAllAnnouncements() {
        try {
            // 获取当前时间戳
            long currentTime = System.currentTimeMillis();
            List<Announcement> announcements = announcementMapper.findAllValid();
            
            // 增加每个公告的查看次数
            for (Announcement announcement : announcements) {
                announcement.setViewCount(announcement.getViewCount() + 1);
                announcement.setCreatedAt(null);
                announcement.setUpdatedAt(null);
                announcement.setPublisherId(null);
                announcementMapper.increaseViewCount(announcement.getId());
            }
            
            return Result.success(announcements);
        } catch (Exception e) {
            e.printStackTrace();
            return Result.error("获取公告列表失败");
        }
    }

    @Override
    public Result getAnnouncementDetail(Integer id) {
        try {
            if (id == null) {
                return Result.error("公告ID不能为空");
            }
            
            Announcement announcement = announcementMapper.findById(id);
            if (announcement == null) {
                return Result.error("公告不存在");
            }
            
            // 增加查看次数
            announcementMapper.increaseViewCount(id);
            
            return Result.success(announcement);
        } catch (Exception e) {
            e.printStackTrace();
            return Result.error("获取公告详情失败");
        }
    }

    @Override
    public Result getAnnouncementsByImportance(Integer importance) {
        try {
            if (importance == null || importance < 1 || importance > 3) {
                return Result.error("重要性参数无效");
            }
            
            // 获取当前时间戳
            long currentTime = System.currentTimeMillis();
            List<Announcement> announcements = announcementMapper.findByImportance(importance);
            
            // 增加每个公告的查看次数
            for (Announcement announcement : announcements) {
                announcement.setViewCount(announcement.getViewCount() + 1);
                announcement.setCreatedAt(null);
                announcement.setUpdatedAt(null);
                announcementMapper.increaseViewCount(announcement.getId());
            }
            
            return Result.success(announcements);
        } catch (Exception e) {
            e.printStackTrace();
            return Result.error("获取公告列表失败");
        }
    }

    @Override
    public Result getLatestAnnouncements(Integer limit) {
        try {
            if (limit == null || limit <= 0) {
                limit = 5; // 默认显示5条
            }
            
            // 获取当前时间戳
            long currentTime = System.currentTimeMillis();
            List<Announcement> announcements = announcementMapper.findLatest(limit);
            
            // 增加每个公告的查看次数
            for (Announcement announcement : announcements) {
                announcement.setViewCount(announcement.getViewCount() + 1);
                announcement.setCreatedAt(null);
                announcement.setUpdatedAt(null);
                announcementMapper.increaseViewCount(announcement.getId());
            }
            
            return Result.success(announcements);
        } catch (Exception e) {
            e.printStackTrace();
            return Result.error("获取最新公告失败");
        }
    }
} 