package com.zcpbysj.campusidletrade_server.entity.dto.admin;

import lombok.Data;

/**
 * 通用状态更新DTO
 */
@Data
public class StatusDTO {
    /**
     * 状态值
     */
    private String status;
    
    /**
     * 原因（可选，用于拒绝等场景）
     */
    private String reason;
}
