package com.panjx.clouddrive.mapper;

import com.panjx.clouddrive.pojo.Admin;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface AdminMapper {
    
    /**
     * 根据管理员名查询管理员
     * @param adminName 管理员名
     * @return 管理员对象，不存在则返回null
     */
    Admin findByAdminName(String adminName);
    
    /**
     * 根据ID查询管理员
     * @param id 管理员ID
     * @return 管理员对象，不存在则返回null
     */
    Admin findById(Long id);
    
    /**
     * 更新管理员最后登录时间
     * @param adminName 管理员名
     * @param loginTime 登录时间
     */
    void updateLoginTime(@Param("adminName") String adminName, @Param("loginTime") Long loginTime);
    
    /**
     * 添加管理员
     * @param admin 管理员对象
     */
    void add(Admin admin);
    
    /**
     * 统计管理员总数
     * @return 管理员总数
     */
    int countAll();
    
    /**
     * 分页查询所有管理员
     * @param offset 偏移量
     * @param pageSize 页大小
     * @return 管理员列表
     */
    List<Admin> findAllByPage(@Param("offset") int offset, @Param("pageSize") int pageSize);
} 