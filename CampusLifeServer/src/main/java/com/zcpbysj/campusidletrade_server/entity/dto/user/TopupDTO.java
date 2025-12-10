package com.zcpbysj.campusidletrade_server.entity.dto.user;

import lombok.Data;

import java.math.BigDecimal;

/**
 * 充值请求DTO
 */
@Data
public class TopupDTO {
    
    /**
     * 充值金额
     */
    private BigDecimal amount;
    
    /**
     * 支付方式
     */
    private String paymentMethod;
}
