package com.zcpbysj.campusidletrade_server.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * 跨域配置类
 * 解决前后端分离开发中的跨域问题
 */
@Configuration
public class CorsConfig implements WebMvcConfigurer {

    /**
     * 跨域配置
     * 配置全局的跨域访问规则，支持前后端分离开发
     */
    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
                // 允许的源地址 - 使用通配符模式支持所有域名
                // 在生产环境中建议指定具体的域名，如：.allowedOrigins("http://localhost:3000", "https://yourdomain.com")
                .allowedOriginPatterns("*")
                // 允许的请求头 - 包括自定义的认证令牌头
                // 支持 Authorization、Token、X-Token 等常用的令牌传递方式
                .allowedHeaders("*")
                // 允许的请求方法 - 支持RESTful API的所有常用方法
                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS", "HEAD", "PATCH")
                // 是否允许发送Cookie和认证信息
                // 设置为true时，前端可以在跨域请求中携带Cookie、Authorization等认证信息
                .allowCredentials(true)
                // 预检请求的缓存时间（秒）
                // OPTIONS预检请求的结果会被浏览器缓存3600秒，减少不必要的预检请求
                .maxAge(3600);
    }

    /**
     * 跨域过滤器配置
     * 提供更细粒度的跨域控制，特别针对登录令牌的处理
     * 这个配置会覆盖上面的全局配置，提供更精确的控制
     */
    @Bean
    public CorsFilter corsFilter() {
        CorsConfiguration config = new CorsConfiguration();
        
        // 允许所有源地址 - 支持开发环境的灵活访问
        // 生产环境建议改为具体的域名列表
        config.addAllowedOriginPattern("*");
        
        // 允许所有请求头 - 特别支持各种令牌传递方式
        // 包括：Authorization、Token、X-Token、X-Auth-Token、Bearer等
        config.addAllowedHeader("*");
        
        // 允许所有请求方法 - 支持完整的RESTful API操作
        config.addAllowedMethod("*");
        
        // 允许发送Cookie和认证信息 - 这是令牌认证的关键配置
        // 当设置为true时，浏览器会在跨域请求中包含认证信息
        config.setAllowCredentials(true);
        
        // 预检请求的缓存时间 - 优化性能，减少重复的OPTIONS请求
        config.setMaxAge(3600L);
        
        // 暴露的响应头（前端可以访问的响应头）
        // 这些头部信息前端JavaScript可以读取，对于令牌刷新等操作很重要
        config.addExposedHeader("Content-Type");           // 内容类型
        config.addExposedHeader("X-Requested-With");       // AJAX请求标识
        config.addExposedHeader("accept");                 // 接受的内容类型
        config.addExposedHeader("Origin");                 // 请求来源
        config.addExposedHeader("Access-Control-Request-Method");   // 预检请求方法
        config.addExposedHeader("Access-Control-Request-Headers");  // 预检请求头
        config.addExposedHeader("Authorization");          // 标准认证头 - JWT令牌常用
        config.addExposedHeader("Token");                  // 自定义令牌头
        config.addExposedHeader("X-Token");                // 另一种常用的令牌头
        config.addExposedHeader("X-Auth-Token");           // 认证令牌头
        config.addExposedHeader("X-Refresh-Token");        // 刷新令牌头
        config.addExposedHeader("X-Total-Count");          // 分页总数（常用于列表接口）
        
        // 注册跨域配置到所有路径
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);
        
        return new CorsFilter(source);
    }
}