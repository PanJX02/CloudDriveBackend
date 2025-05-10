package com.panjx.clouddrive.service.admin;

import com.panjx.clouddrive.pojo.Admin;
import com.panjx.clouddrive.pojo.AdminDTO;
import com.panjx.clouddrive.pojo.Result;
import com.panjx.clouddrive.pojo.request.AdminUpdateUserInfoRequest;
import com.panjx.clouddrive.pojo.request.PageRequest;
import com.panjx.clouddrive.pojo.request.UpdateAdminRequest;
import com.panjx.clouddrive.pojo.request.UpdateFileRequest;
import com.panjx.clouddrive.pojo.request.UpdateShareRequest;
import jakarta.servlet.http.HttpServletRequest;

public interface AdminService {
    /**
     * 管理员登录
     * 
     * @param adminDTO 管理员登录信息
     * @return 登录结果
     */
    Result login(AdminDTO adminDTO);
    
    /**
     * 添加管理员
     * 
     * @param adminDTO 管理员信息
     * @return 添加结果
     */
    Result addAdmin(AdminDTO adminDTO);
    
    /**
     * 根据管理员名称查找管理员
     * 
     * @param adminName 管理员名称
     * @return 管理员信息
     */
    Admin findByAdminName(String adminName);
    
    /**
     * 根据ID查找管理员
     * 
     * @param id 管理员ID
     * @return 管理员信息
     */
    Admin findById(Long id);
    
    /**
     * 获取所有分享信息
     * 
     * @param pageRequest 分页请求参数
     * @return 分享信息列表
     */
    Result getAllShares(PageRequest pageRequest);
    
    /**
     * 更新分享信息
     * 
     * @param updateShareRequest 更新分享请求参数
     * @return 更新结果
     */
    Result updateShare(UpdateShareRequest updateShareRequest);
    
    /**
     * 管理员获取所有文件信息（分页）
     * 
     * @param pageRequest 分页请求参数
     * @return 文件列表结果
     */
    Result getAllFiles(PageRequest pageRequest);
    
    /**
     * 管理员修改文件信息
     * 
     * @param updateFileRequest 文件信息修改请求
     * @return 修改结果
     */
    Result updateFileInfo(UpdateFileRequest updateFileRequest);

    /**
     * 刷新管理员令牌
     *
     * @param request HttpServletRequest
     * @return 结果
     */
    Result refreshToken(HttpServletRequest request);
    
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
    
    /**
     * 获取所有管理员信息（分页）
     *
     * @param pageRequest 分页请求参数
     * @return 管理员列表结果
     */
    Result getAllAdmins(PageRequest pageRequest);
    
    /**
     * 更新管理员信息
     *
     * @param updateAdminRequest 更新管理员请求参数
     * @return 更新结果
     */
    Result updateAdmin(UpdateAdminRequest updateAdminRequest);
} 