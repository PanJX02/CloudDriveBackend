package com.panjx.clouddrive.utils;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;
import java.util.Random;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;

/**
 * 分享工具类
 */
public class ShareUtil {
    
    // AES加密密钥(16位)
    private static final String AES_KEY = "CloudDrive@2023!";
    // 初始化向量
    private static final String AES_IV = "CloudShare@2023!";
    // 标识加密标识类型的前缀
    private static final String PREFIX_CODE = "C";    // 带提取码的前缀
    private static final String PREFIX_NOCODE = "N";  // 不带提取码的前缀
    
    /**
     * 生成分享ID (长整型)
     * @return 分享ID
     */
    public static Long generateShareId() {
        // 生成16位数字ID
        long timestamp = System.currentTimeMillis(); // 13位时间戳
        int random = ThreadLocalRandom.current().nextInt(100000, 999999); // 6位随机数
        
        // 组合时间戳和随机数，保证ID唯一性
        return timestamp * 1000000L + random; // 19位长整型
    }
    
    /**
     * 生成提取码
     * @return 提取码
     */
    public static String generateCode() {
        String chars = "ABCDEFGHJKLMNPQRSTUVWXYZabcdefghjkmnpqrstuvwxyz123456789";
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
     * 生成不包含提取码的加密标识（需要单独提供提取码）
     * @param shareId 分享ID
     * @return 加密后的标识
     */
    public static String encryptShareId(Long shareId) {
        try {
            // 准备加密内容，只包含分享ID
            String content = PREFIX_NOCODE + ":" + shareId;
            
            // 使用AES加密
            byte[] encrypted = aesEncrypt(content);
            
            // Base64编码使结果URL友好
            return Base64.getUrlEncoder().encodeToString(encrypted);
        } catch (Exception e) {
            // 出现异常时使用简单编码替代
            return PREFIX_NOCODE + Base64.getUrlEncoder().encodeToString(shareId.toString().getBytes(StandardCharsets.UTF_8));
        }
    }
    
    /**
     * 生成包含提取码的加密标识
     * @param shareId 分享ID
     * @param code 提取码
     * @return 加密后的标识
     */
    public static String encryptShareIdWithCode(Long shareId, String code) {
        try {
            // 准备加密内容，包含分享ID和提取码
            String content = PREFIX_CODE + ":" + shareId + ":" + code;
            
            // 使用AES加密
            byte[] encrypted = aesEncrypt(content);
            
            // Base64编码使结果URL友好
            return Base64.getUrlEncoder().encodeToString(encrypted);
        } catch (Exception e) {
            // 出现异常时使用简单编码替代
            return PREFIX_CODE + Base64.getUrlEncoder().encodeToString((shareId + ":" + code).getBytes(StandardCharsets.UTF_8));
        }
    }
    
    /**
     * 从不包含提取码的加密标识中提取分享ID
     * @param encryptedKey 加密标识
     * @return 分享ID，解析失败返回null
     */
    public static Long getShareIdFromKey(String encryptedKey) {
        try {
            // Base64解码
            byte[] decrypted = aesDecrypt(Base64.getUrlDecoder().decode(encryptedKey));
            
            // 解析内容
            String content = new String(decrypted, StandardCharsets.UTF_8);
            
            // 验证前缀
            if (!content.startsWith(PREFIX_NOCODE + ":")) {
                return null;
            }
            
            // 提取分享ID
            String[] parts = content.split(":");
            if (parts.length >= 2) {
                return Long.parseLong(parts[1]);
            }
            
            return null;
        } catch (Exception e) {
            return null;
        }
    }
    
    /**
     * 从包含提取码的加密标识中提取分享ID和提取码
     * @param encryptedKeyWithCode 加密标识
     * @return 包含shareId和code的数组，索引0为shareId，索引1为code，解析失败返回null
     */
    public static Object[] getShareInfoFromKeyWithCode(String encryptedKeyWithCode) {
        try {
            // Base64解码
            byte[] decrypted = aesDecrypt(Base64.getUrlDecoder().decode(encryptedKeyWithCode));
            
            // 解析内容
            String content = new String(decrypted, StandardCharsets.UTF_8);
            
            // 验证前缀
            if (!content.startsWith(PREFIX_CODE + ":")) {
                return null;
            }
            
            // 提取分享ID和提取码
            String[] parts = content.split(":");
            if (parts.length >= 3) {
                Long shareId = Long.parseLong(parts[1]);
                String code = parts[2];
                return new Object[] { shareId, code };
            }
            
            return null;
        } catch (Exception e) {
            return null;
        }
    }
    
    /**
     * 校验加密标识是否为不包含提取码的分享形式
     * @param encryptedKey 加密标识
     * @return 是否为不包含提取码的加密标识
     */
    public static boolean isShareKeyWithoutCode(String encryptedKey) {
        try {
            // Base64解码
            byte[] decrypted = aesDecrypt(Base64.getUrlDecoder().decode(encryptedKey));
            
            // 解析内容
            String content = new String(decrypted, StandardCharsets.UTF_8);
            
            // 检查是否以不包含提取码标识开头
            return content.startsWith(PREFIX_NOCODE + ":");
        } catch (Exception e) {
            return false;
        }
    }
    
    /**
     * 校验加密标识是否为包含提取码的分享形式
     * @param encryptedKey 加密标识
     * @return 是否为包含提取码的加密标识
     */
    public static boolean isShareKeyWithCode(String encryptedKey) {
        try {
            // Base64解码
            byte[] decrypted = aesDecrypt(Base64.getUrlDecoder().decode(encryptedKey));
            
            // 解析内容
            String content = new String(decrypted, StandardCharsets.UTF_8);
            
            // 检查是否以包含提取码标识开头
            return content.startsWith(PREFIX_CODE + ":");
        } catch (Exception e) {
            return false;
        }
    }
    
    /**
     * AES加密
     * @param content 待加密内容
     * @return 加密后的字节数组
     */
    private static byte[] aesEncrypt(String content) throws Exception {
        Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
        SecretKeySpec keySpec = new SecretKeySpec(AES_KEY.getBytes(StandardCharsets.UTF_8), "AES");
        IvParameterSpec ivSpec = new IvParameterSpec(AES_IV.getBytes(StandardCharsets.UTF_8));
        
        cipher.init(Cipher.ENCRYPT_MODE, keySpec, ivSpec);
        return cipher.doFinal(content.getBytes(StandardCharsets.UTF_8));
    }
    
    /**
     * AES解密
     * @param encrypted 加密的字节数组
     * @return 解密后的字节数组
     */
    private static byte[] aesDecrypt(byte[] encrypted) throws Exception {
        Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
        SecretKeySpec keySpec = new SecretKeySpec(AES_KEY.getBytes(StandardCharsets.UTF_8), "AES");
        IvParameterSpec ivSpec = new IvParameterSpec(AES_IV.getBytes(StandardCharsets.UTF_8));
        
        cipher.init(Cipher.DECRYPT_MODE, keySpec, ivSpec);
        return cipher.doFinal(encrypted);
    }
} 