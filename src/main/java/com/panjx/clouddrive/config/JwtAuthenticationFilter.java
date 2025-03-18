package com.panjx.clouddrive.config;

import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.panjx.clouddrive.utils.JwtUtil;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        
        String token = getTokenFromRequest(request);
        
        if (StringUtils.hasText(token)) {
            try {
                // 验证token
                System.out.println("token: " + token);
                DecodedJWT decodedJWT = JwtUtil.verifyToken(token);
                
                // 获取用户ID和用户名
                Long userId = Long.parseLong(decodedJWT.getSubject());
                String username = decodedJWT.getClaim("username").asString();
                
                // 创建认证对象，这里简单地授予"USER"角色
                UsernamePasswordAuthenticationToken authenticationToken = 
                        new UsernamePasswordAuthenticationToken(
                                username, 
                                null, 
                                List.of(new SimpleGrantedAuthority("ROLE_USER")));
                
                // 将userId存储在details中
                authenticationToken.setDetails(userId);
                
                // 设置认证信息到上下文
                SecurityContextHolder.getContext().setAuthentication(authenticationToken);
                
            } catch (JWTVerificationException e) {
                // token验证失败，不设置认证信息
                logger.error("Token验证失败: " + e.getMessage());
            }
        }
        
        filterChain.doFilter(request, response);
    }
    
    private String getTokenFromRequest(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");
        // 检查请求头中是否包含"Bearer "
        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith("Bearer ")) {
            // 返回token（从第七位开始返回）
            return bearerToken.substring(7);
        }
        return null;
    }
} 