package com.zcpbysj.campusidletrade_server.entity.dto.user;

import lombok.Data;

/**
 * 注册请求DTO
 */
@Data
public class RegisterDTO {
    
    /**
     * 学号
     */
    private String studentId;
    
    /**
     * 用户名
     */
    private String username;
    
    /**
     * 密码
     */
    private String password;
    
    /**
     * 邮箱
     */
    private String email;
}
