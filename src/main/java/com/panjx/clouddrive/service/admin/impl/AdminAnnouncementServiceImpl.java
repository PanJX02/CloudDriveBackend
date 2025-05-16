package com.panjx.clouddrive.service.admin.impl;

import com.panjx.clouddrive.mapper.AnnouncementMapper;
import com.panjx.clouddrive.pojo.Announcement;
import com.panjx.clouddrive.pojo.Result;
import com.panjx.clouddrive.pojo.request.CreateAnnouncementRequest;
import com.panjx.clouddrive.pojo.request.PageRequest;
import com.panjx.clouddrive.pojo.request.UpdateAnnouncementRequest;
import com.panjx.clouddrive.pojo.response.AnnouncementList;
import com.panjx.clouddrive.pojo.response.PageMeta;
import com.panjx.clouddrive.service.admin.AdminAnnouncementService;
import com.panjx.clouddrive.utils.SecurityUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 管理员公告服务实现类
 */
@Service
public class AdminAnnouncementServiceImpl implements AdminAnnouncementService {

    @Autowired
    private AnnouncementMapper announcementMapper;

    @Override
    public Result getAllAnnouncements(PageRequest pageRequest) {
        try {
            int page = pageRequest.getPage();
            int pageSize = pageRequest.getPageSize();
            
            // 参数校验
            if (page < 1) {
                page = 1;
            }
            if (pageSize < 1) {
                pageSize = 10;
            }
            
            // 计算偏移量
            int offset = (page - 1) * pageSize;
            
            // 获取总记录数
            int total = announcementMapper.countAll();
            
            // 计算总页数
            int totalPage = (total + pageSize - 1) / pageSize;
            
            // 获取分页数据
            List<Announcement> announcements = announcementMapper.findAllByPage(offset, pageSize);
            
            // 构建分页元数据
            PageMeta pageMeta = new PageMeta(total, totalPage, pageSize, page);
            
            // 构建返回对象
            AnnouncementList announcementList = new AnnouncementList(announcements, pageMeta);
            
            return Result.success(announcementList);
        } catch (Exception e) {
            e.printStackTrace();
            return Result.error("获取公告列表失败");
        }
    }
    
    @Override
    public Result updateAnnouncement(UpdateAnnouncementRequest request) {
        try {
            // 参数校验
            if (request.getId() == null) {
                return Result.error("公告ID不能为空");
            }
            
            // 查询是否存在该公告
            Announcement existingAnnouncement = announcementMapper.findById(request.getId());
            if (existingAnnouncement == null) {
                return Result.error("公告不存在");
            }
            
            // 构建更新对象
            Announcement announcement = new Announcement();
            announcement.setId(request.getId());
            announcement.setTitle(request.getTitle());
            announcement.setContent(request.getContent());
            announcement.setExpiryTime(request.getExpiryTime());
            announcement.setImportance(request.getImportance());
            
            // 处理状态和发布时间
            Integer status = request.getStatus();
            if (status != null) {
                announcement.setStatus(status);
                
                // 如果状态为已发布(1)且原公告的发布时间为空，则设置发布时间为当前时间
                if (status == 1 && existingAnnouncement.getPublishTime() == null) {
                    announcement.setPublishTime(System.currentTimeMillis());
                }
            }
            
            // 设置更新时间
            announcement.setUpdatedAt(System.currentTimeMillis());
            
            // 执行更新
            int rows = announcementMapper.updateAnnouncement(announcement);
            if (rows > 0) {
                return Result.success("修改公告成功");
            } else {
                return Result.error("修改公告失败");
            }
        } catch (Exception e) {
            e.printStackTrace();
            return Result.error("修改公告失败：" + e.getMessage());
        }
    }
    
    @Override
    public Result createAnnouncement(CreateAnnouncementRequest request) {
        try {
            // 参数校验
            if (request.getTitle() == null || request.getTitle().trim().isEmpty()) {
                return Result.error("公告标题不能为空");
            }
            if (request.getContent() == null || request.getContent().trim().isEmpty()) {
                return Result.error("公告内容不能为空");
            }
            
            // 获取当前管理员ID
            Long adminId = SecurityUtil.getCurrentAdminId();
            if (adminId == null) {
                return Result.error("未获取到管理员信息，请重新登录");
            }
            
            // 构建公告对象
            Announcement announcement = new Announcement();
            announcement.setTitle(request.getTitle());
            announcement.setContent(request.getContent());
            
            // 设置状态，默认为已发布(1)
            Integer status = request.getStatus();
            if (status == null || status < 0 || status > 2) {
                status = 1;
            }
            announcement.setStatus(status);
            
            // 如果状态为已发布(1)，则设置发布时间为当前时间
            if (status == 1) {
                announcement.setPublishTime(System.currentTimeMillis());
            } else {
                announcement.setPublishTime(null);
            }
            
            // 设置过期时间
            announcement.setExpiryTime(request.getExpiryTime());
            
            // 设置管理员ID（转换为Integer）
            announcement.setPublisherId(adminId.intValue());
            
            // 设置重要性，默认为普通(1)
            Integer importance = request.getImportance();
            if (importance == null || importance < 1 || importance > 3) {
                importance = 1;
            }
            announcement.setImportance(importance);
            
            // 设置查看次数为0
            announcement.setViewCount(0);
            
            // 设置创建时间和更新时间
            Long currentTime = System.currentTimeMillis();
            announcement.setCreatedAt(currentTime);
            announcement.setUpdatedAt(currentTime);
            
            // 执行插入
            int rows = announcementMapper.insertAnnouncement(announcement);
            if (rows > 0) {
                return Result.success("创建公告成功");
            } else {
                return Result.error("创建公告失败");
            }
        } catch (Exception e) {
            e.printStackTrace();
            return Result.error("创建公告失败：" + e.getMessage());
        }
    }
} 