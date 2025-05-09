package com.panjx.clouddrive.service.impl;

import com.panjx.clouddrive.mapper.AdminMapper;
import com.panjx.clouddrive.pojo.Admin;
import com.panjx.clouddrive.service.AdminQueryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class AdminQueryServiceImpl implements AdminQueryService {

    @Autowired
    private AdminMapper adminMapper;

    @Override
    public Admin findByAdminName(String adminName) {
        return adminMapper.findByAdminName(adminName);
    }

    @Override
    public Admin findById(Long id) {
        return adminMapper.findById(id);
    }
} 