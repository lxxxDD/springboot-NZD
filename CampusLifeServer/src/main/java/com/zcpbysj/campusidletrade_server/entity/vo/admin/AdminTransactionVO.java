package com.zcpbysj.campusidletrade_server.entity.vo.admin;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 管理端交易记录VO（包含用户信息）
 */
@Data
public class AdminTransactionVO {
    
    private Long id;
    
    /**
     * 用户ID
     */
    private Long userId;
    
    /**
     * 用户名
     */
    private String username;
    
    /**
     * 用户头像
     */
    private String avatar;
    
    /**
     * 交易金额
     */
    private BigDecimal amount;
    
    /**
     * 交易类型
     */
    private String type;
    
    /**
     * 交易描述
     */
    private String description;
    
    /**
     * 交易后余额
     */
    private BigDecimal balanceAfter;
    
    /**
     * 交易时间
     */
    private LocalDateTime createTime;
}
