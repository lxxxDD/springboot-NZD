package com.zcpbysj.campusidletrade_server.entity.dto.order;

import lombok.Data;

import java.math.BigDecimal;

/**
 * 创建订单请求DTO
 */
@Data
public class CreateOrderDTO {
    
    /**
     * 商品ID
     */
    private Long itemId;
    
    /**
     * 订单类型: food/market
     */
    private String type;
    
    /**
     * 订单金额
     */
    private BigDecimal amount;
}
