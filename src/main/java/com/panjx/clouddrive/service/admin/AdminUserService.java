package com.panjx.clouddrive.service.admin;

import com.panjx.clouddrive.pojo.Result;
import com.panjx.clouddrive.pojo.request.AdminUpdateUserInfoRequest;
import com.panjx.clouddrive.pojo.request.PageRequest;

public interface AdminUserService {
    
    /**
     * 获取所有用户信息（分页）
     * 
     * @param pageRequest 分页请求参数
     * @return 用户列表结果
     */
    Result getAllUsers(PageRequest pageRequest);
    
    /**
     * 获取用户详情
     * 
     * @param userId 用户ID
     * @return 用户详情
     */
    Result getUserDetail(Long userId);
    
    /**
     * 管理员修改用户信息
     * 
     * @param request 用户信息修改请求
     * @return 修改结果
     */
    Result updateUserInfo(AdminUpdateUserInfoRequest request);
} 