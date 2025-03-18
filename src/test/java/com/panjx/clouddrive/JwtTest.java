package com.panjx.clouddrive;

import com.panjx.clouddrive.pojo.User;
import com.panjx.clouddrive.utils.JwtUtil;
import org.junit.jupiter.api.Test;

public class JwtTest {

    @Test
    public void testGenerateToken() {
        User user = new User();
        user.setUserId(1L);
        user.setUserName("panjx");
        user.setPassword("123456");
        String token = JwtUtil.generateToken(user);
        System.out.println(token);
        System.out.println(JwtUtil.getUsernameFromToken(token));
        System.out.println(JwtUtil.getUserIdFromToken(token));
        System.out.println(JwtUtil.verifyToken(token));
    }
}
