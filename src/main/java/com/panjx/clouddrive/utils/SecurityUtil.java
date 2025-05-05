package com.panjx.clouddrive.utils;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

/**
 * 安全工具类，用于处理当前登录用户的信息获取
 */
public class SecurityUtil {

    /**
     * 获取当前登录用户的用户名
     * @return 用户名，未登录时返回null
     */
    public static String getCurrentUsername() {
        Authentication authentication = getAuthentication();
        if (isValidAuthentication(authentication)) {
            return authentication.getName();
        }
        return null;
    }

    /**
     * 获取当前登录用户的ID
     * @return 用户ID，未登录时返回null
     */
    public static Long getCurrentUserId() {
        Authentication authentication = getAuthentication();
        if (isValidAuthentication(authentication) && authentication.getDetails() instanceof Long) {
            return (Long) authentication.getDetails();
        }
        return null;
    }

    /**
     * 判断当前用户是否已认证
     * @return 是否已认证
     */
    public static boolean isAuthenticated() {
        return isValidAuthentication(getAuthentication());
    }
    
    /**
     * 获取当前认证信息
     * @return 认证对象
     */
    private static Authentication getAuthentication() {
        return SecurityContextHolder.getContext().getAuthentication();
    }
    
    /**
     * 验证认证对象是否有效
     * @param authentication 认证对象
     * @return 是否有效
     */
    private static boolean isValidAuthentication(Authentication authentication) {
        return authentication != null && authentication.isAuthenticated();
    }
} 