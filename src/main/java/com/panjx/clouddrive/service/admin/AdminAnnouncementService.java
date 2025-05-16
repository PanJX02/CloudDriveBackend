package com.panjx.clouddrive.service.admin;

import com.panjx.clouddrive.pojo.Result;
import com.panjx.clouddrive.pojo.request.CreateAnnouncementRequest;
import com.panjx.clouddrive.pojo.request.PageRequest;
import com.panjx.clouddrive.pojo.request.UpdateAnnouncementRequest;

/**
 * 管理员公告服务接口
 */
public interface AdminAnnouncementService {
    
    /**
     * 获取所有公告信息（分页）
     * @param pageRequest 分页请求参数
     * @return 分页后的公告列表
     */
    Result getAllAnnouncements(PageRequest pageRequest);
    
    /**
     * 修改公告信息
     * @param request 修改公告请求参数
     * @return 修改结果
     */
    Result updateAnnouncement(UpdateAnnouncementRequest request);
    
    /**
     * 创建公告
     * @param request 创建公告请求参数
     * @return 创建结果
     */
    Result createAnnouncement(CreateAnnouncementRequest request);
} 