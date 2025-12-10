package com.zcpbysj.campusidletrade_server.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springdoc.core.models.GroupedOpenApi;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Knife4j(Swagger) API文档配置
 * 访问地址：http://localhost:8080/doc.html
 */
@Configuration
public class Knife4jConfig {

    /**
     * 配置 API 文档基本信息
     */
    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("校园生活服务平台 API 文档")
                        .version("1.0.0")
                        .description("校园生活服务平台接口文档，包含用户管理、食堂订餐、二手交易、社交动态、报修服务等模块")
                        .contact(new Contact()
                                .name("开发团队")
                                .email("dev@campus.com")
                                .url("http://campus.com"))
                        .license(new License()
                                .name("Apache 2.0")
                                .url("http://www.apache.org/licenses/LICENSE-2.0.html")))
                // 配置认证方式
                .components(new Components()
                        .addSecuritySchemes("Bearer Token", new SecurityScheme()
                                .type(SecurityScheme.Type.HTTP)
                                .scheme("bearer")
                                .bearerFormat("JWT")
                                .in(SecurityScheme.In.HEADER)
                                .name("Authorization")
                                .description("请输入 JWT Token，格式：Bearer {token}")))
                // 全局应用安全配置
                .addSecurityItem(new SecurityRequirement().addList("Bearer Token"));
    }

    /**
     * 全部接口
     */
    @Bean
    public GroupedOpenApi allApi() {
        return GroupedOpenApi.builder()
                .group("全部接口")
                .pathsToMatch("/api/**")
                .build();
    }

    /**
     * 用户管理模块
     */
    @Bean
    public GroupedOpenApi userApi() {
        return GroupedOpenApi.builder()
                .group("用户管理")
                .pathsToMatch("/api/user/**", "/api/admin/**")
                .build();
    }

    /**
     * 食堂订餐模块
     */
    @Bean
    public GroupedOpenApi canteenApi() {
        return GroupedOpenApi.builder()
                .group("食堂订餐")
                .pathsToMatch("/api/canteen/**", "/api/stall/**", "/api/dish/**", "/api/order/**")
                .build();
    }

    /**
     * 二手交易模块
     */
    @Bean
    public GroupedOpenApi goodsApi() {
        return GroupedOpenApi.builder()
                .group("二手交易")
                .pathsToMatch("/api/goods-category/**", "/api/goods/**")
                .build();
    }

    /**
     * 社交动态模块
     */
    @Bean
    public GroupedOpenApi socialApi() {
        return GroupedOpenApi.builder()
                .group("社交动态")
                .pathsToMatch("/api/post/**")
                .build();
    }

    /**
     * 报修服务模块
     */
    @Bean
    public GroupedOpenApi repairApi() {
        return GroupedOpenApi.builder()
                .group("报修服务")
                .pathsToMatch("/api/repair-order/**")
                .build();
    }

    /**
     * 系统管理模块
     */
    @Bean
    public GroupedOpenApi systemApi() {
        return GroupedOpenApi.builder()
                .group("系统管理")
                .pathsToMatch("/api/banner/**", "/api/system/**")
                .build();
    }
}

