package com.panjx.clouddrive.utils;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;
import java.util.Random;
import java.util.UUID;

/**
 * 分享工具类
 */
public class ShareUtil {
    
    // 固定盐值增加安全性
    private static final String SALT = "cloudDrive2023";
    // 带提取码的额外盐值
    private static final String CODE_SALT = "WithCode";
    // 不带提取码的额外盐值
    private static final String NO_CODE_SALT = "DirectAccess";
    
    /**
     * 生成分享ID
     * @return 分享ID
     */
    public static String generateShareId() {
        return UUID.randomUUID().toString().replace("-", "").substring(0, 16);
    }
    
    /**
     * 生成提取码
     * @return 提取码
     */
    public static String generateCode() {
        String chars = "ABCDEFGHJKLMNPQRSTUVWXYZabcdefghjkmnpqrstuvwxyz23456789";
        StringBuilder sb = new StringBuilder();
        Random random = new Random();
        
        for (int i = 0; i < 4; i++) {
            int index = random.nextInt(chars.length());
            sb.append(chars.charAt(index));
        }
        
        return sb.toString();
    }
    
    /**
     * 根据有效期类型计算过期时间
     * @param validType 有效期类型 0:1天 1:7天 2:30天 3:永久
     * @return 过期时间的时间戳
     */
    public static Long calculateExpireTime(Integer validType) {
        long currentTime = System.currentTimeMillis();
        
        switch (validType) {
            case 0: // 1天
                return currentTime + 24 * 60 * 60 * 1000L;
            case 1: // 7天
                return currentTime + 7 * 24 * 60 * 60 * 1000L;
            case 2: // 30天
                return currentTime + 30 * 24 * 60 * 60 * 1000L;
            case 3: // 永久
                return null;
            default:
                return currentTime + 24 * 60 * 60 * 1000L; // 默认1天
        }
    }
    
    /**
     * 对分享ID进行哈希加密（不包含提取码，需要单独提供提取码）
     * @param shareId 原始分享ID
     * @return 加密后的分享标识
     */
    public static String encryptShareId(String shareId) {
        try {
            // 添加固定盐值和带提取码标识增加安全性
            String saltedShareId = shareId + SALT + NO_CODE_SALT;
            
            // 使用SHA-256算法进行哈希
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(saltedShareId.getBytes(StandardCharsets.UTF_8));
            
            // 使用Base64编码使其URL友好
            String base64Hash = Base64.getUrlEncoder().encodeToString(hash);
            
            // 取前20位作为加密标识
            return base64Hash.substring(0, 20);
        } catch (NoSuchAlgorithmException e) {
            // 如果算法不可用，返回原始ID的Base64编码
            return Base64.getUrlEncoder().encodeToString((shareId + NO_CODE_SALT).getBytes(StandardCharsets.UTF_8));
        }
    }
    
    /**
     * 对分享ID进行哈希加密（包含提取码，无需单独提供提取码）
     * @param shareId 原始分享ID
     * @param code 提取码（将编码到分享标识中）
     * @return 加密后的分享标识
     */
    public static String encryptShareIdWithCode(String shareId, String code) {
        try {
            // 添加固定盐值、提取码和无需提取码标识增加安全性
            String saltedShareId = shareId + SALT + CODE_SALT + code;
            
            // 使用SHA-256算法进行哈希
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(saltedShareId.getBytes(StandardCharsets.UTF_8));
            
            // 使用Base64编码使其URL友好
            String base64Hash = Base64.getUrlEncoder().encodeToString(hash);
            
            // 取前25位作为加密标识（比需要提取码的长一些，增加安全性）
            return base64Hash.substring(0, 25);
        } catch (NoSuchAlgorithmException e) {
            // 如果算法不可用，返回原始ID的Base64编码
            return Base64.getUrlEncoder().encodeToString((shareId + CODE_SALT + code).getBytes(StandardCharsets.UTF_8));
        }
    }
    
    /**
     * 校验加密标识是否为不包含提取码的分享形式
     * @param encryptedId 加密标识
     * @return 是否为不包含提取码的加密标识
     */
    public static boolean isShareKeyWithoutCode(String encryptedId) {
        // 简单通过长度判断加密标识类型
        return encryptedId != null && encryptedId.length() == 20;
    }
    
    /**
     * 校验加密标识是否为包含提取码的分享形式
     * @param encryptedId 加密标识
     * @return 是否为包含提取码的加密标识
     */
    public static boolean isShareKeyWithCode(String encryptedId) {
        // 简单通过长度判断加密标识类型
        return encryptedId != null && encryptedId.length() == 25;
    }
} 