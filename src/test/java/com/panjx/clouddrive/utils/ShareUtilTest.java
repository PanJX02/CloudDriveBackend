package com.panjx.clouddrive.utils;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.junit.jupiter.api.Assertions.*;

/**
 * ShareUtil工具类的测试类
 */
public class ShareUtilTest {

    @Test
    @DisplayName("测试生成提取码")
    public void testGenerateCode() {
        // 生成提取码
        String code = ShareUtil.generateCode();
        System.out.println("生成的提取码: " + code);
        
        // 验证长度为4
        assertEquals(4, code.length());
        
        // 验证不同调用返回不同结果
        String code2 = ShareUtil.generateCode();
        System.out.println("生成的第二个提取码: " + code2);
        assertNotEquals(code, code2);
    }
    
    @ParameterizedTest
    @ValueSource(ints = {0, 1, 2, 3})
    @DisplayName("测试计算过期时间")
    public void testCalculateExpireTime(int validType) {
        System.out.println("\n测试有效期类型: " + validType);
        long currentTime = System.currentTimeMillis();
        System.out.println("当前时间戳: " + currentTime);
        
        Long expireTime = ShareUtil.calculateExpireTime(validType);
        System.out.println("计算的过期时间: " + expireTime);
        
        switch (validType) {
            case 0: // 1天
                assertTrue(expireTime > currentTime + 23 * 60 * 60 * 1000L);
                assertTrue(expireTime < currentTime + 25 * 60 * 60 * 1000L);
                System.out.println("有效期为1天，过期时间合理");
                break;
            case 1: // 7天
                assertTrue(expireTime > currentTime + 6 * 24 * 60 * 60 * 1000L);
                assertTrue(expireTime < currentTime + 8 * 24 * 60 * 60 * 1000L);
                System.out.println("有效期为7天，过期时间合理");
                break;
            case 2: // 30天
                assertTrue(expireTime > currentTime + 29 * 24 * 60 * 60 * 1000L);
                assertTrue(expireTime < currentTime + 31 * 24 * 60 * 60 * 1000L);
                System.out.println("有效期为30天，过期时间合理");
                break;
            case 3: // 永久
                assertNull(expireTime);
                System.out.println("有效期为永久，过期时间为null");
                break;
        }
    }
    
    @Test
    @DisplayName("测试不带提取码的加密和解密")
    public void testEncryptAndDecryptShareId() {
        System.out.println("\n开始测试不带提取码的加密和解密");
        // 准备测试数据
        Long originalShareId = 1234567890L;
        System.out.println("原始分享ID: " + originalShareId);
        
        // 加密
        String encryptedKey = ShareUtil.encryptShareId(originalShareId);
        System.out.println("加密后的标识: " + encryptedKey);
        assertNotNull(encryptedKey);
        
        // 解密
        Long decryptedShareId = ShareUtil.getShareIdFromKey(encryptedKey);
        System.out.println("解密后的分享ID: " + decryptedShareId);
        assertNotNull(decryptedShareId);
        
        // 验证解密后的结果与原始值相同
        assertEquals(originalShareId, decryptedShareId);
        System.out.println("解密结果与原始ID匹配: " + (originalShareId.equals(decryptedShareId)));
        
        // 验证类型判断正确
        boolean isWithoutCode = ShareUtil.isShareKeyWithoutCode(encryptedKey);
        boolean isWithCode = ShareUtil.isShareKeyWithCode(encryptedKey);
        System.out.println("是否为不含提取码标识: " + isWithoutCode);
        System.out.println("是否为含提取码标识: " + isWithCode);
        assertTrue(isWithoutCode);
        assertFalse(isWithCode);
    }
    
    @Test
    @DisplayName("测试带提取码的加密和解密")
    public void testEncryptAndDecryptShareIdWithCode() {
        System.out.println("\n开始测试带提取码的加密和解密");
        // 准备测试数据
        Long originalShareId = 9876543210L;
        String originalCode = "Abc1";
        System.out.println("原始分享ID: " + originalShareId);
        System.out.println("原始提取码: " + originalCode);
        
        // 加密
        String encryptedKeyWithCode = ShareUtil.encryptShareIdWithCode(originalShareId, originalCode);
        System.out.println("加密后的标识: " + encryptedKeyWithCode);
        assertNotNull(encryptedKeyWithCode);
        
        // 解密
        Object[] decryptedInfo = ShareUtil.getShareInfoFromKeyWithCode(encryptedKeyWithCode);
        assertNotNull(decryptedInfo);
        assertEquals(2, decryptedInfo.length);
        
        // 验证解密后的结果与原始值相同
        Long decryptedShareId = (Long) decryptedInfo[0];
        String decryptedCode = (String) decryptedInfo[1];
        System.out.println("解密后的分享ID: " + decryptedShareId);
        System.out.println("解密后的提取码: " + decryptedCode);
        
        assertEquals(originalShareId, decryptedShareId);
        assertEquals(originalCode, decryptedCode);
        System.out.println("解密结果与原始ID匹配: " + (originalShareId.equals(decryptedShareId)));
        System.out.println("解密结果与原始提取码匹配: " + (originalCode.equals(decryptedCode)));
        
        // 验证类型判断正确
        boolean isWithCode = ShareUtil.isShareKeyWithCode(encryptedKeyWithCode);
        boolean isWithoutCode = ShareUtil.isShareKeyWithoutCode(encryptedKeyWithCode);
        System.out.println("是否为含提取码标识: " + isWithCode);
        System.out.println("是否为不含提取码标识: " + isWithoutCode);
        assertTrue(isWithCode);
        assertFalse(isWithoutCode);
    }
    
    @Test
    @DisplayName("测试加密标识互不干扰")
    public void testEncryptedKeysDoNotInterfere() {
        System.out.println("\n开始测试加密标识互不干扰");
        // 准备测试数据
        Long shareId = 123456789L;
        String code = "Test";
        System.out.println("测试分享ID: " + shareId);
        System.out.println("测试提取码: " + code);
        
        // 生成两种类型的加密标识
        String shareKey = ShareUtil.encryptShareId(shareId);
        String shareKeyWithCode = ShareUtil.encryptShareIdWithCode(shareId, code);
        System.out.println("不含提取码标识: " + shareKey);
        System.out.println("含提取码标识: " + shareKeyWithCode);
        
        // 测试不能从普通标识中解析出提取码信息
        Object[] info = ShareUtil.getShareInfoFromKeyWithCode(shareKey);
        System.out.println("尝试从不含提取码标识中解析提取码信息: " + (info == null ? "失败(预期行为)" : "成功(错误行为)"));
        assertNull(info);
        
        // 测试不能从带提取码标识中解析出普通信息
        Long extractedId = ShareUtil.getShareIdFromKey(shareKeyWithCode);
        System.out.println("尝试从含提取码标识中解析普通信息: " + (extractedId == null ? "失败(预期行为)" : "成功(错误行为)"));
        assertNull(extractedId);
    }
    
    @Test
    @DisplayName("测试无效输入的处理")
    public void testInvalidInput() {
        System.out.println("\n开始测试无效输入的处理");
        
        // 无效字符串
        String invalidKey = "invalid-key";
        System.out.println("测试无效字符串: " + invalidKey);
        
        Long extractedId = ShareUtil.getShareIdFromKey(invalidKey);
        System.out.println("尝试解析分享ID: " + (extractedId == null ? "返回null(预期行为)" : extractedId));
        assertNull(extractedId);
        
        Object[] extractedInfo = ShareUtil.getShareInfoFromKeyWithCode(invalidKey);
        System.out.println("尝试解析提取码信息: " + (extractedInfo == null ? "返回null(预期行为)" : "返回数据(错误行为)"));
        assertNull(extractedInfo);
        
        // 空值
        System.out.println("\n测试null值");
        extractedId = ShareUtil.getShareIdFromKey(null);
        System.out.println("尝试解析null为分享ID: " + (extractedId == null ? "返回null(预期行为)" : extractedId));
        assertNull(extractedId);
        
        extractedInfo = ShareUtil.getShareInfoFromKeyWithCode(null);
        System.out.println("尝试解析null为提取码信息: " + (extractedInfo == null ? "返回null(预期行为)" : "返回数据(错误行为)"));
        assertNull(extractedInfo);
        
        // 判断类型
        boolean isWithoutCode = ShareUtil.isShareKeyWithoutCode(invalidKey);
        boolean isWithCode = ShareUtil.isShareKeyWithCode(invalidKey);
        System.out.println("无效字符串是否为不含提取码标识: " + isWithoutCode);
        System.out.println("无效字符串是否为含提取码标识: " + isWithCode);
        assertFalse(isWithoutCode);
        assertFalse(isWithCode);
    }
} 