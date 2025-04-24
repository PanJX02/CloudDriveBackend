package com.panjx.clouddrive.controller;

import com.panjx.clouddrive.pojo.Result;
import com.panjx.clouddrive.service.AnnouncementService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * 公告相关接口控制器
 */
@Slf4j
@RestController
@RequestMapping("/announcements")
public class AnnouncementController {

    @Autowired
    private AnnouncementService announcementService;
    
    /**
     * 获取所有有效公告
     * @return 公告列表
     */
    @GetMapping
    public Result getAllAnnouncements() {
        log.info("获取所有公告");
        return announcementService.getAllAnnouncements();
    }
    
    /**
     * 获取公告详情
     * @param id 公告ID
     * @return 公告详情
     */
    @GetMapping("/{id}")
    public Result getAnnouncementDetail(@PathVariable Integer id) {
        log.info("获取公告详情，ID: {}", id);
        return announcementService.getAnnouncementDetail(id);
    }
    
    /**
     * 获取指定重要性级别的公告
     * @param importance 重要性级别：1=普通，2=重要，3=紧急
     * @return 公告列表
     */
    @GetMapping("/importance/{importance}")
    public Result getAnnouncementsByImportance(@PathVariable Integer importance) {
        log.info("获取重要性为{}的公告", importance);
        return announcementService.getAnnouncementsByImportance(importance);
    }
    
    /**
     * 获取最新的N条公告
     * @param limit 限制条数
     * @return 公告列表
     */
    @GetMapping("/latest")
    public Result getLatestAnnouncements(@RequestParam(required = false, defaultValue = "5") Integer limit) {
        log.info("获取最新的{}条公告", limit);
        return announcementService.getLatestAnnouncements(limit);
    }
} 