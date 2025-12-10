package com.zcpbysj.campusidletrade_server.interceptor;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.zcpbysj.campusidletrade_server.common.Result;
import com.zcpbysj.campusidletrade_server.entity.User;
import com.zcpbysj.campusidletrade_server.service.IUserService;
import com.zcpbysj.campusidletrade_server.util.JwtUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

/**
 * 用户状态拦截器
 * 检查用户是否被禁用
 */
@Component
public class UserStatusInterceptor implements HandlerInterceptor {

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private IUserService userService;

    @Autowired
    private ObjectMapper objectMapper;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        // 获取token
        String token = request.getHeader("Authorization");
        if (token == null || token.isEmpty()) {
            return true; // 没有token，让后续逻辑处理
        }

        // 处理 Bearer 前缀
        if (token.startsWith("Bearer ")) {
            token = token.substring(7);
        }

        try {
            // 获取用户ID
            Long userId = jwtUtil.getUserIdFromToken(token);
            if (userId == null) {
                return true;
            }

            // 查询用户状态
            User user = userService.getById(userId);
            if (user == null) {
                writeErrorResponse(response, "用户不存在");
                return false;
            }

            // 检查用户状态（0=禁用，1=正常）
            if (user.getStatus() != null && user.getStatus() == 0) {
                writeErrorResponse(response, "您的账号已被禁用，请联系管理员");
                return false;
            }

            // 将用户信息存入request
            request.setAttribute("userId", userId);
            request.setAttribute("currentUser", user);

        } catch (Exception e) {
            // token解析失败，让后续逻辑处理
            return true;
        }

        return true;
    }

    private void writeErrorResponse(HttpServletResponse response, String message) throws Exception {
        response.setStatus(403);
        response.setContentType("application/json;charset=UTF-8");
        Result<?> result = Result.fail(message);
        response.getWriter().write(objectMapper.writeValueAsString(result));
    }
}
