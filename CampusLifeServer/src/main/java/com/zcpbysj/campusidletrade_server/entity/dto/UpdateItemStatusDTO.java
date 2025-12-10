package com.zcpbysj.campusidletrade_server.entity.dto;

import lombok.Data;

/**
 * 更新商品状态DTO
 */
@Data
public class UpdateItemStatusDTO {
    
    /**
     * 状态: active/inactive/sold
     */
    private String status;
}
