package com.zcpbysj.campusidletrade_server.entity.vo.user;

import lombok.Data;

/**
 * 登录响应VO
 */
@Data
public class LoginVO {
    
    /**
     * 用户ID
     */
    private Long userId;
    
    /**
     * 用户信息
     */
    private UserInfoVO userInfo;
    
    /**
     * JWT Token
     */
    private String token;
}
