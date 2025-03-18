package com.panjx.clouddrive.utils;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.panjx.clouddrive.pojo.User;

import java.util.Date;

public class JwtUtil {
    // 密钥（实际项目中应放在配置文件中）
    private static final String SECRET_KEY = "20020920";
    // 过期时间（24小时）
    private static final long EXPIRE_TIME = 24 * 60 * 60 * 1000;

    /**
     * 生成JWT令牌
     * @param user 用户对象
     * @return JWT令牌
     */
    public static String generateToken(User user) {
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + EXPIRE_TIME);
        
        return JWT.create()
                .withSubject(user.getUserId().toString())
                .withClaim("username", user.getUserName())
                .withClaim("nickname", user.getNickName())
                .withIssuedAt(now)
                .withExpiresAt(expiryDate)
                .sign(Algorithm.HMAC256(SECRET_KEY));
    }

    /**
     * 验证JWT令牌
     * @param token JWT令牌
     * @return 解码后的JWT
     * @throws JWTVerificationException 如果验证失败
     */
    public static DecodedJWT verifyToken(String token) throws JWTVerificationException {
        return JWT.require(Algorithm.HMAC256(SECRET_KEY)).build().verify(token);
    }

    /**
     * 从JWT令牌中获取用户ID
     * @param token JWT令牌
     * @return 用户ID
     */
    public static Long getUserIdFromToken(String token) {
        DecodedJWT jwt = verifyToken(token);
        return Long.parseLong(jwt.getSubject());
    }

    /**
     * 从JWT令牌中获取用户名
     * @param token JWT令牌
     * @return 用户名
     */
    public static String getUsernameFromToken(String token) {
        DecodedJWT jwt = verifyToken(token);
        return jwt.getClaim("username").asString();
    }
} 