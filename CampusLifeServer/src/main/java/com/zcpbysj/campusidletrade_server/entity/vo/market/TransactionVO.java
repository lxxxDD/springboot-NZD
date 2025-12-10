package com.zcpbysj.campusidletrade_server.entity.vo.market;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 交易记录VO
 */
@Data
public class TransactionVO {
    
    private Long id;
    
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
