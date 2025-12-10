package com.zcpbysj.campusidletrade_server.entity.dto.order;

import lombok.Data;

/**
 * 支付订单请求DTO
 */
@Data
public class PayOrderDTO {
    
    /**
     * 支付方式
     */
    private String paymentMethod;
}
