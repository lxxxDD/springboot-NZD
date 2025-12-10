package com.zcpbysj.campusidletrade_server.config;

import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * 静态资源跨域配置
 * 专门为图片等静态资源添加 CORS 头，支持前端 AI 图像分析等场景
 */
@Configuration
public class StaticResourceCorsConfig {

    @Bean
    public FilterRegistrationBean<Filter> staticResourceCorsFilter() {
        FilterRegistrationBean<Filter> registration = new FilterRegistrationBean<>();
        
        registration.setFilter(new Filter() {
            @Override
            public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
                    throws IOException, ServletException {
                HttpServletRequest httpRequest = (HttpServletRequest) request;
                HttpServletResponse httpResponse = (HttpServletResponse) response;
                
                String path = httpRequest.getRequestURI();
                
                // 为静态资源（图片、上传文件、头像）添加 CORS 头
                if (path.startsWith("/uploads/") || path.startsWith("/avatar/") || 
                    path.endsWith(".png") || path.endsWith(".jpg") || path.endsWith(".jpeg") ||
                    path.endsWith(".gif") || path.endsWith(".webp")) {
                    
                    // 允许所有来源访问（支持 crossorigin="anonymous"）
                    httpResponse.setHeader("Access-Control-Allow-Origin", "*");
                    httpResponse.setHeader("Access-Control-Allow-Methods", "GET, OPTIONS");
                    httpResponse.setHeader("Access-Control-Allow-Headers", "*");
                    httpResponse.setHeader("Access-Control-Max-Age", "3600");
                    
                    // 处理 OPTIONS 预检请求
                    if ("OPTIONS".equalsIgnoreCase(httpRequest.getMethod())) {
                        httpResponse.setStatus(HttpServletResponse.SC_OK);
                        return;
                    }
                }
                
                chain.doFilter(request, response);
            }
        });
        
        // 设置过滤器优先级（最高优先级）
        registration.setOrder(Integer.MIN_VALUE);
        // 过滤所有请求
        registration.addUrlPatterns("/*");
        registration.setName("staticResourceCorsFilter");
        
        return registration;
    }
}
