package com.panjx.clouddrive.service;

import com.panjx.clouddrive.pojo.Admin;
import org.springframework.stereotype.Service;

@Service
public interface AdminQueryService {
    Admin findByAdminName(String adminName);
    Admin findById(Long id);
} 