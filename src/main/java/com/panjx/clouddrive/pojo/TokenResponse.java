package com.panjx.clouddrive.pojo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 令牌响应对象
 */
@Data
@NoArgsConstructor
public class TokenResponse {
    private String accessToken;
    private String refreshToken;

    //用于登录、注册
    public TokenResponse(String accessToken, String refreshToken) {
        this.accessToken = accessToken;
        this.refreshToken = refreshToken;
    }

    //用于刷新
    public TokenResponse(String accessToken) {
        this.accessToken = accessToken;
    }
} 