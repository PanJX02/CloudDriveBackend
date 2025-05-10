package com.panjx.clouddrive.service.admin;

import com.panjx.clouddrive.pojo.Result;
import com.panjx.clouddrive.pojo.request.PageRequest;
import org.springframework.stereotype.Service;

@Service
public interface AdminListService {
    /**
     * 获取所有管理员信息（分页）
     * 
     * @param pageRequest 分页请求参数
     * @return 管理员列表结果
     */
    Result getAllAdmins(PageRequest pageRequest);
} 