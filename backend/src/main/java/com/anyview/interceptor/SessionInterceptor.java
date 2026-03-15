package com.anyview.interceptor;

import com.anyview.config.JwtTokenProvider;
import com.anyview.service.SessionService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

@Component
@RequiredArgsConstructor
public class SessionInterceptor implements HandlerInterceptor {

    private final SessionService sessionService;
    private final JwtTokenProvider tokenProvider;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        String token = request.getHeader("Authorization");
        
        if (token != null && token.startsWith("Bearer ")) {
            token = token.substring(7);
            
            try {
                String username = tokenProvider.getUsernameFromToken(token);
                if (username != null) {
                    // 从token中获取用户ID（这里简化处理，实际可能需要从数据库查询）
                    // 暂时使用username作为userId的替代
                    sessionService.updateLastActivity(username);
                }
            } catch (Exception e) {
                // token解析失败，忽略
            }
        }
        
        return true;
    }
}