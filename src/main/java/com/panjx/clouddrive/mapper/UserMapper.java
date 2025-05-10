package com.panjx.clouddrive.mapper;

import com.panjx.clouddrive.pojo.User;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface UserMapper {
    // 根据用户名查询用户
    User findByUsername(String username);

    // 根据用户ID查询用户
    User findById(Long userId);

    // 注册
    void add(User user);

    //更新用户使用空间
    void updateUserSpace(long userId, long space);
    
    //更新用户信息
    void updateUserInfo(User user);
    
    //更新用户密码
    void updatePassword(User user);
    
    // 查询所有用户（管理员使用）
    List<User> findAll();
    
    // 根据邮箱查询用户
    User findByEmail(String email);
    
    // 管理员更新用户信息
    void updateUserInfoByAdmin(User user);
    
    // 获取用户总数量
    int countAllUsers();
    
    // 分页查询用户
    List<User> getUsersByPage(@Param("offset") int offset, @Param("pageSize") int pageSize);
}
