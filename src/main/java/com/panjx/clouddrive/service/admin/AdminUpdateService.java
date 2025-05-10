package com.panjx.clouddrive.service.admin;

import com.panjx.clouddrive.pojo.Result;
import com.panjx.clouddrive.pojo.request.UpdateAdminRequest;
import org.springframework.stereotype.Service;

@Service
public interface AdminUpdateService {
    /**
     * 更新管理员信息
     * 
     * @param updateAdminRequest 更新管理员请求参数
     * @return 更新结果
     */
    Result updateAdmin(UpdateAdminRequest updateAdminRequest);
} 