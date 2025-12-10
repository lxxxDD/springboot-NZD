package com.zcpbysj.campusidletrade_server.entity.dto.user;

import lombok.Data;

/**
 * 登录请求DTO
 */
@Data
public class LoginDTO {
    
    /**
     * 学号
     */
    private String studentId;
    
    /**
     * 密码
     */
    private String password;
}
