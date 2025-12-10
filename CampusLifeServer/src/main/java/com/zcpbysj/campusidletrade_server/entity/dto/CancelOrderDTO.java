package com.zcpbysj.campusidletrade_server.entity.dto;

import lombok.Data;

/**
 * 取消订单请求DTO
 */
@Data
public class CancelOrderDTO {
    
    /**
     * 取消原因
     */
    private String reason;
}
