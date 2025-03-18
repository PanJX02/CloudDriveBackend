package com.panjx.clouddrive.utils;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

/**
 * 密码工具类，提供密码加密和验证功能
 */
public class PasswordUtil {
    
    // 默认加密强度
    private static final int DEFAULT_STRENGTH = 10;
    
    // 默认使用标准强度的编码器
    private static BCryptPasswordEncoder encoder = new BCryptPasswordEncoder(DEFAULT_STRENGTH);
    
    /**
     * 设置加密强度
     * @param strength 加密强度，范围4-31，默认为10
     *                 强度越高，加密耗时越长，安全性越高
     */
    public static void setStrength(int strength) {
        if (strength < 4 || strength > 31) {
            throw new IllegalArgumentException("加密强度必须在4到31之间");
        }
        encoder = new BCryptPasswordEncoder(strength);
    }
    
    /**
     * 获取当前加密强度
     * @return 当前加密强度
     */
    public static int getStrength() {
        return DEFAULT_STRENGTH;
    }
    
    /**
     * 对密码进行加密
     * @param rawPassword 原始密码
     * @return 加密后的密码
     */
    public static String encode(String rawPassword) {
        return encoder.encode(rawPassword);
    }
    
    /**
     * 使用指定强度对密码进行加密
     * @param rawPassword 原始密码
     * @param strength 加密强度，范围4-31
     * @return 加密后的密码
     */
    public static String encodeWithStrength(String rawPassword, int strength) {
        if (strength < 4 || strength > 31) {
            throw new IllegalArgumentException("加密强度必须在4到31之间");
        }
        BCryptPasswordEncoder customEncoder = new BCryptPasswordEncoder(strength);
        return customEncoder.encode(rawPassword);
    }
    
    /**
     * 验证密码是否匹配
     * @param rawPassword 原始密码
     * @param encodedPassword 加密后的密码
     * @return 如果匹配返回true，否则返回false
     */
    public static boolean matches(String rawPassword, String encodedPassword) {
        return encoder.matches(rawPassword, encodedPassword);
    }
} 