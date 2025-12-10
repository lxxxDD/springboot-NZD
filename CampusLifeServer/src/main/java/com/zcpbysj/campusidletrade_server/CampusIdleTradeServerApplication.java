package com.zcpbysj.campusidletrade_server;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * 校园生活服务应用启动类
 */
@SpringBootApplication
@MapperScan("com.zcpbysj.campusidletrade_server.mapper")
public class CampusIdleTradeServerApplication {

    public static void main(String[] args) {
        SpringApplication.run(CampusIdleTradeServerApplication.class, args);
    }

}