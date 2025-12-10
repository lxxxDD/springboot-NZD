package com.zcpbysj.campusidletrade_server.entity.dto.market;

import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

/**
 * 更新商品请求DTO
 */
@Data
public class UpdateMarketItemDTO {
    
    /**
     * 商品标题
     */
    private String title;
    
    /**
     * 价格
     */
    private BigDecimal price;
    
    /**
     * 原价
     */
    private BigDecimal originalPrice;
    
    /**
     * 分类
     */
    private String category;
    
    /**
     * 成色
     */
    private String conditionLevel;
    
    /**
     * 图片列表
     */
    private List<String> images;
    
    /**
     * 商品描述
     */
    private String description;
}
