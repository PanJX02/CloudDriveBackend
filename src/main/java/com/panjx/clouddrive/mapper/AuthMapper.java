package com.panjx.clouddrive.mapper;

import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface AuthMapper {

    void updateLoginTime(String username, Long loginTime);
}
