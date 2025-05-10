package com.panjx.clouddrive.service.admin;

import com.panjx.clouddrive.pojo.AdminDTO;
import com.panjx.clouddrive.pojo.Result;
import org.springframework.stereotype.Service;

@Service
public interface AdminLoginService {
    Result login(AdminDTO adminDTO);
} 