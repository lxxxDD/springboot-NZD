package com.zcpbysj.campusidletrade_server.entity.vo.user;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 用户信息VO
 */
@Data
public class UserInfoVO {
    
    private Long id;
    
    /**
     * 学号
     */
    private String studentId;
    
    /**
     * 用户名
     */
    private String username;
    
    /**
     * 邮箱
     */
    private String email;
    
    /**
     * 手机号
     */
    private String phone;
    
    /**
     * 头像URL
     */
    private String avatar;
    
    /**
     * 钱包余额
     */
    private BigDecimal balance;
    
    /**
     * 积分
     */
    private Integer points;
    
    /**
     * 创建时间
     */
    private LocalDateTime createTime;
}
