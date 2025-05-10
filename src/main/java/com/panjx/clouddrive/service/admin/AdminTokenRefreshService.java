package com.panjx.clouddrive.service.admin;

import com.panjx.clouddrive.pojo.Result;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Service;

@Service
public interface AdminTokenRefreshService {
    Result refreshToken(HttpServletRequest request);
} 