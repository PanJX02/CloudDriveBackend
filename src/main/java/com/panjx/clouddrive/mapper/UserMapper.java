package com.panjx.clouddrive.mapper;

import com.panjx.clouddrive.pojo.User;
import org.apache.ibatis.annotations.Mapper;

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
}
