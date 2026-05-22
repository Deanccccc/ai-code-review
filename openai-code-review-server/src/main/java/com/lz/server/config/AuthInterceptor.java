package com.lz.server.config;

import com.lz.server.model.Result;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * @Author: lz
 * @Date: 2026/4/20 15:15
 * @Description: 认证拦截器，用于验证内部API的Token
 */
@Component
public class AuthInterceptor implements HandlerInterceptor {

    @Value("${internal.api-token}")
    private String internalApiToken;

    /**
     * 请求预处理拦截
     * 对/api/review/submit接口进行Token认证
     *
     * @param request  HTTP请求
     * @param response HTTP响应
     * @param handler  处理器
     * @return boolean 是否继续处理
     */
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        String requestUri = request.getRequestURI();
        
        if ("OPTIONS".equalsIgnoreCase(request.getMethod())) {
            return true;
        }
        
        if (requestUri.contains("/submit")) {
            String authHeader = request.getHeader("Authorization");
            
            if (authHeader == null || !authHeader.startsWith("Bearer ")) {
                sendForbiddenResponse(response, "缺少认证令牌");
                return false;
            }

            String token = authHeader.substring(7);
            if (!internalApiToken.equals(token)) {
                sendForbiddenResponse(response, "无效的认证令牌");
                return false;
            }
        }
        
        return true;
    }

    /**
     * 发送禁止访问响应
     *
     * @param response HTTP响应
     * @param message  错误消息
     */
    private void sendForbiddenResponse(HttpServletResponse response, String message) throws IOException {
        response.setStatus(HttpServletResponse.SC_FORBIDDEN);
        response.setContentType("application/json;charset=UTF-8");
        Result<Void> result = Result.error(403, message);
        response.getWriter().write(String.format("{\"code\":%d,\"message\":\"%s\",\"data\":null}",
                result.getCode(), result.getMessage()));
    }
}
