package com.panjx.clouddrive.service;

import com.panjx.clouddrive.pojo.AdminDTO;
import com.panjx.clouddrive.pojo.Result;
import org.springframework.stereotype.Service;

@Service
public interface AdminAddService {
    Result addAdmin(AdminDTO adminDTO);
} 