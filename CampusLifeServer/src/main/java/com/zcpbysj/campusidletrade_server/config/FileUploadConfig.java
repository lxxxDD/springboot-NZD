package com.zcpbysj.campusidletrade_server.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.io.File;

/**
 * 文件上传配置类
 * 配置静态资源映射，使上传的文件可以通过URL访问
 */
@Configuration
public class FileUploadConfig implements WebMvcConfigurer {

    @Value("${file.upload.path:uploads}")
    private String uploadPath;

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // 获取绝对路径
        String absolutePath = new File(uploadPath).getAbsolutePath() + File.separator;
        
        // 配置静态资源映射
        // 访问路径：/uploads/**
        // 映射到：实际的文件目录
        registry.addResourceHandler("/uploads/**")
                .addResourceLocations("file:" + absolutePath);
        
        // 配置默认头像静态资源映射
        registry.addResourceHandler("/avatar/**")
                .addResourceLocations("classpath:/static/avatar/");
        
        System.out.println("==============================================");
        System.out.println("📁 文件上传目录配置成功！");
        System.out.println("📂 上传路径：" + absolutePath);
        System.out.println("🌐 访问地址：http://localhost:8080/uploads/");
        System.out.println("👤 默认头像：http://localhost:8080/avatar/1.png ~ 4.png");
        System.out.println("==============================================");
    }
}

