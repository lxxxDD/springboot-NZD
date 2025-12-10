package com.zcpbysj.campusidletrade_server.config;

import com.zcpbysj.campusidletrade_server.interceptor.UserStatusInterceptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * Web MVC 配置
 * 注册拦截器
 */
@Configuration
public class WebMvcConfig implements WebMvcConfigurer {

    @Autowired
    private UserStatusInterceptor userStatusInterceptor;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        // 用户状态拦截器 - 只拦截用户端 API
        registry.addInterceptor(userStatusInterceptor)
                .addPathPatterns("/api/**")           // 拦截所有 /api/ 路径
                .excludePathPatterns(
                        "/api/auth/**",               // 排除登录注册等认证接口
                        "/api/admin/**",              // 排除管理后台接口
                        "/api/public/**",             // 排除公开接口
                        "/uploads/**"                 // 排除上传文件访问
                );
    }
}
