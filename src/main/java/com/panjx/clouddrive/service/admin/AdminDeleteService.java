package com.panjx.clouddrive.service.admin;

import com.panjx.clouddrive.pojo.Result;

/**
 * 管理员删除服务接口
 */
public interface AdminDeleteService {
    
    /**
     * 删除管理员
     *
     * @param id 要删除的管理员ID
     * @return 删除结果
     */
    Result deleteAdmin(Long id);
} 