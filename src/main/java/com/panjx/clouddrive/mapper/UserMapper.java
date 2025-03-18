package com.panjx.clouddrive.mapper;

import com.panjx.clouddrive.pojo.User;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface UserMapper {
    // 根据用户名查询用户
    User findByUsername(String username);

    // 注册
    void add(User user);
}
