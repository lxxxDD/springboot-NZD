package com.zcpbysj.campusidletrade_server.entity.dto.repair;

import lombok.Data;

/**
 * 评价报修请求DTO
 */
@Data
public class RateRepairDTO {
    
    /**
     * 评分 1-5
     */
    private Integer rating;
    
    /**
     * 反馈
     */
    private String feedback;
}
