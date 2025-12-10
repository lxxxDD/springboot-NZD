package com.zcpbysj.campusidletrade_server.entity.dto.user;

import lombok.Data;

/**
 * 更新用户信息DTO
 */
@Data
public class UpdateProfileDTO {
    
    /**
     * 用户名
     */
    private String username;
    
    /**
     * 头像URL
     */
    private String avatar;
    
    /**
     * 手机号
     */
    private String phone;
}
